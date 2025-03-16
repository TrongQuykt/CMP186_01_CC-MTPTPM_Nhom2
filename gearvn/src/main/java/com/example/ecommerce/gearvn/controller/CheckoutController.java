package com.example.ecommerce.gearvn.controller;

import com.example.ecommerce.gearvn.model.*;
import com.example.ecommerce.gearvn.repository.*;
import com.example.ecommerce.gearvn.service.DiscountService;
import com.example.ecommerce.gearvn.service.ProductService;
import com.example.ecommerce.gearvn.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMethodRepository shipmentMethodRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private AddressRepository addressRepository;

    private static final String VNPAY_HASH_SECRET = "BFRBXJCBLCYXBAYZDJDUSXWABIYXWONM";
    private static final String VNPAY_TMN_CODE = "2VK9KT2C";
    private static final String VNPAY_PAYMENT_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNPAY_RETURN_URL = "http://localhost:8080/checkout/vnpay_return";

    @GetMapping
    public String showCheckoutPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Cart> cartOptional = Optional.ofNullable(cartRepository.findByUser(user));
        if (cartOptional.isEmpty()) {
            return "redirect:/cart";
        }

        List<Address> addresses = addressRepository.findByUser(user);
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        List<ShipmentMethod> shipmentMethods = shipmentMethodRepository.findAll();

        Cart cart = cartOptional.get();
        model.addAttribute("cart", cart);
        model.addAttribute("totalQuantity", cart.getTotalQuantity());
        model.addAttribute("addresses", addresses);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("shipmentMethods", shipmentMethods);
        return "checkout";
    }

    @PostMapping
    public RedirectView processCheckout(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam("paymentMethodId") Long paymentMethodId,
                                        @RequestParam("shipmentMethodId") Long shipmentMethodId,
                                        @RequestParam("addressId") Long addressId,
                                        @RequestParam("discountCode") String discountCode,
                                        HttpServletRequest request) throws Exception {
        User user = userService.getCurrentUser(userDetails);
        if (user == null) {
            return new RedirectView("/login");
        }

        Optional<Cart> cartOptional = Optional.ofNullable(cartRepository.findByUser(user));
        if (cartOptional.isEmpty()) {
            return new RedirectView("/cart");
        }

        Cart cart = cartOptional.get();
        System.out.println("Initial Cart Total Price: " + cart.getTotalPrice()); // Log tổng tiền ban đầu của giỏ hàng

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(new Date());

        double total = cart.getTotalPrice();
        Discount discount = null;

        if (!discountCode.isEmpty()) {
            discount = discountService.findByCode(discountCode);
            if (discount != null && discount.getUseNumber() < discount.getQuantity()) {
                total -= discount.getDiscountPrice();
                discount.setUseNumber(discount.getUseNumber() + 1);
                discountService.updateDiscount(discount);
                order.setDiscount(discount);
                System.out.println("Discount Applied: " + discount.getDiscountPrice()); // Log giá trị giảm giá
            }
        }

        double shippingFee = 0;
        if (shipmentMethodId == 1) {
            shippingFee = 35000;
        } else if (shipmentMethodId == 2) {
            shippingFee = 45000;
        }
        total += shippingFee;

        System.out.println("Shipping Fee: " + shippingFee); // Log phí vận chuyển
        System.out.println("Total After Shipping and Discount: " + total); // Log tổng tiền sau khi thêm phí vận chuyển và giảm giá

        order.setTotal(total);
        order.setStatus(1);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            productService.updateProductSoldQuantity(cartItem.getProduct(), cartItem.getQuantity());

            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethodRepository.findById(paymentMethodId).orElse(null));
        payment.setAmount(order.getTotal());
        payment.setPaymentDate(new Date());
        payment.setStatus(1);
        paymentRepository.save(payment);

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setShipmentMethod(shipmentMethodRepository.findById(shipmentMethodId).orElse(null));
        shipment.setTrackingNumber(generateRandomString(20));
        shipment.setShipmentDate(new Date());
        shipment.setStatus(1);
        shipmentRepository.save(shipment);

        cart.getItems().clear();
        cartRepository.save(cart);

        if (paymentMethodId == 1) {
            order.setStatus(2);
            orderRepository.save(order);
            return new RedirectView("/order/" + order.getId());
        } else if (paymentMethodId == 2) {
            return new RedirectView(createVnpayPaymentUrl(order, request));
        }

        return new RedirectView("/order/" + order.getId());
    }



    private String generateRandomString(int length) {
        final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    private String createVnpayPaymentUrl(Order order, HttpServletRequest request) throws Exception {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Thanh toan don hang: " + order.getId();
        String orderType = "other";
        String vnp_TxnRef = order.getId().toString();
        String vnp_IpAddr = getClientIp(request);
        String vnp_TmnCode = VNPAY_TMN_CODE;

        // Nhân với 100 để chuyển sang đơn vị đồng
        String vnp_Amount = String.valueOf((long) (order.getTotal() * 100));  // Nhân với 100
        String vnp_ReturnUrl = VNPAY_RETURN_URL;

        System.out.println("VNPay Amount: " + vnp_Amount);  // Log giá trị gửi sang VNPay

        // Các phần còn lại không cần thay đổi
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append('&');
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').
                        append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append('&');
            }
        }
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(VNPAY_HASH_SECRET, hashData.toString());

        System.out.println("VNPay Secure Hash: " + vnp_SecureHash);  // Log mã bảo mật

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VNPAY_PAYMENT_URL + "?" + queryUrl;
    }








    private static String hmacSHA512(String key, String data) throws Exception {
        Mac sha512Hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512Hmac.init(keySpec);
        byte[] macData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : macData) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }



    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    @GetMapping("/vnpay_return")
    public String vnpayReturn(@RequestParam Map<String, String> params, Model model) throws Exception {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append('&');
            }
        }
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }
        String secureHash = hmacSHA512(VNPAY_HASH_SECRET, hashData.toString());
        if (secureHash.equals(vnp_SecureHash)) {
            String orderId = params.get("vnp_TxnRef");
            Optional<Order> orderOptional = orderRepository.findById(Long.parseLong(orderId));
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setStatus(2);
                orderRepository.save(order);
                model.addAttribute("message", "Thanh toán thành công!");
                model.addAttribute("orderId", orderId);
                return "redirect:/order/" + orderId;
            }
        }
        model.addAttribute("message", "Thanh toán không thành công!");
        model.addAttribute("orderId", params.get("vnp_TxnRef"));
        return "failure";
    }




    @PostMapping("/add-address")
    public String addAddress(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String street,
                             @RequestParam String ward,
                             @RequestParam String district,
                             @RequestParam String city) {
        User user = userService.getCurrentUser(userDetails);
        if (user == null) {
            return "redirect:/login";
        }

        Address address = new Address();
        address.setUser(user);
        address.setStreet(street);
        address.setWard(ward);
        address.setDistrict(district);
        address.setCity(city);

        addressRepository.save(address);

        return "redirect:/checkout";
    }

    @GetMapping("/check-discount")
    @ResponseBody
    public Map<String, Object> checkDiscount(@RequestParam String code) {
        Map<String, Object> response = new HashMap<>();
        Discount discount = discountService.findByCode(code);
        if (discount == null || discount.getUseNumber() >= discount.getQuantity()) {
            response.put("valid", false);
            response.put("message", "Mã giảm giá không tồn tại hoặc đã hết lượt sử dụng");
        } else {
            response.put("valid", true);
            response.put("discountPrice", discount.getDiscountPrice());
            response.put("message", "Mã giảm giá đã được áp dụng!");
        }
        return response;
    }



}
