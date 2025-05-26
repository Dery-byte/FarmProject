//package com.alibou.book.Services.OrderServicesImpl;
//
//import com.alibou.book.DTO.FarmersOrdersDTO.OrderDTO;
//import com.alibou.book.Entity.Order;
//import com.alibou.book.Entity.OrderDetails;
//import com.alibou.book.Repositories.OrderRepository;
//import com.alibou.book.Repositories.ProductRepository;
//import com.alibou.book.exception.ResourceNotFoundException;
//import com.alibou.book.exception.UnauthorizedAccessException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import com.alibou.book.DTO.FarmersOrdersDTO.OrderItemDTO;
//import org.springframework.stereotype.Service;
//
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final ProductRepository productRepository;
//
////    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
////        this.orderRepository = orderRepository;
////        this.productRepository = productRepository;
////    }
//
//    @Override
//    public List<Order> getOrdersByFarmer(Long farmerId) {
//        return orderRepository.findByFarmerId(farmerId);
//    }
//
//    @Override
//    public Page<Order> getOrdersByFarmer(Long farmerId, Pageable pageable) {
//        return orderRepository.findByFarmerId(farmerId, pageable);
//    }
//
//    @Override
//    public OrderDTO getOrderDetails(Long orderId, Long farmerId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//        // Verify that at least one item in the order belongs to this farmer
//        boolean belongsToFarmer = order.getOrderDetails().stream()
//                .anyMatch(item -> item.getProduct().getFarmer().getId().equals(farmerId));
//
//        if (!belongsToFarmer) {
//            throw new UnauthorizedAccessException("You don't have permission to view this order");
//        }
//
//        return convertToDTO(order, farmerId);
//    }
//
//    private OrderDTO convertToDTO(Order order, Long farmerId) {
//        OrderDTO dto = new OrderDTO();
//        dto.setOrderId(order.getId());
//        dto.setOrderDate(order.getOrderDate());
//        dto.setCustomerName(order.getCustomer().getFullName());
//        dto.setStatus(order.getStatus());
//       // dto.setTotalAmount(order.getTotalAmount());
//        dto.setTotalAmount(BigDecimal.valueOf(order.getAmount()));
//
//        // Only include items that belong to this farmer
//        List<OrderItemDTO> items = order.getOrderDetails().stream()
//                .filter(item -> item.getProduct().getFarmer().getId().equals(farmerId))
//                .map(this::convertItemToDTO)
//                .collect(Collectors.toList());
//
//        dto.setItems(items);
//        return dto;
//    }
//
//    private OrderItemDTO convertItemToDTO(OrderDetails item) {
//        OrderItemDTO dto = new OrderItemDTO();
//        dto.setProductId(item.getProduct().getId());
//        dto.setProductName(item.getProduct().getProductName());
//        dto.setQuantity(item.getQuantity());
//      //  dto.setPriceAtPurchase(item.getPriceAtPurchase());
//        dto.setPriceAtPurchase(BigDecimal.valueOf(item.getPrice()));
//        // Convert double price to BigDecimal first
//        BigDecimal price = BigDecimal.valueOf(item.getPrice());
//        dto.setPriceAtPurchase(price);
//        BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));
//        dto.setTotal(total);
//        return dto;
//    }
//}