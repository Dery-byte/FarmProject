package com.alibou.book.Services;

import com.alibou.book.DTO.MonthlyReturnSummary;
import com.alibou.book.DTO.ReturnItemDTO;
import com.alibou.book.DTO.ReturnRequestDTO;
import com.alibou.book.Entity.*;
import com.alibou.book.Repositories.*;
import com.alibou.book.exception.ResourceNotFoundException;
import com.alibou.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReturnService {
    private final ReturnRequestRepository returnRequestRepository;
    private final UserDetailsService userDetailsService;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final PaymentRepository paymentRepository;
    private final ReturnItemRepository returnItemRepository;

    public ReturnRequest createReturnRequest(ReturnRequestDTO returnRequestDTO, Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to create a return request.");
        }

        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        // Verify the order belongs to the user
        Order order = orderRepository.findByIdAndCustomer(returnRequestDTO.getOrderId(), user)
                .orElseThrow(() -> new RuntimeException("Order not found or doesn't belong to user"));

        // Check if order is eligible for return
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.RETURNED) {
            throw new RuntimeException("Order is not eligible for return");
        }

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setUser(user);
        returnRequest.setOrderId(order.getId());
        returnRequest.setReason(returnRequestDTO.getReason());
        returnRequest.setRequestDate(LocalDateTime.now());

        for (ReturnItemDTO itemDTO : returnRequestDTO.getItems()) {
            // Verify each product is in the order
            OrderDetails orderDetail = orderDetailsRepository.findByOrderIdAndProductId(order.getId(), itemDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found in order"));

            // Check if item is already returned
            if (orderDetail.getStatus() == OrderDetailStatus.RETURN_REQUESTED) {
                throw new RuntimeException("Product " + orderDetail.getProduct().getProductName() + " already returned");
            }

            // Create return item with RETURN_REQUESTED status
            ReturnItem item = new ReturnItem();
            item.setId(orderDetail.getId());
            item.setProduct(orderDetail.getProduct());
            item.setProduct(orderDetail.getProduct());
            item.setName(orderDetail.getProduct().getProductName());
            item.setQuantity(String.valueOf(orderDetail.getQuantity()));
            item.setReason(itemDTO.getReason());
            item.setStatus(ReturnItemStatus.RETURN_REQUESTED);
            item.setReturnRequest(returnRequest);

            returnRequest.getItems().add(item);

            // Mark order item as return requested
            orderDetail.setStatus(OrderDetailStatus.RETURN_REQUESTED);
            orderDetailsRepository.save(orderDetail);
        }

        return returnRequestRepository.save(returnRequest);
    }







    public void approveReturnItem(Long returnRequestId, Long itemId) {
        ReturnItem returnItem = (ReturnItem) returnItemRepository.findByIdAndReturnRequestId(itemId, returnRequestId)
                .orElseThrow(() -> new RuntimeException("Return item not found"));

        if (returnItem.getStatus() != ReturnItemStatus.PENDING) {
            throw new RuntimeException("Return item already processed");
        }

        // Update return item status
        returnItem.setStatus(ReturnItemStatus.APPROVED);
        returnItem.setProcessedDate(LocalDateTime.now());
        returnItemRepository.save(returnItem);

        // Update corresponding order item
        OrderDetails orderDetail = orderDetailsRepository.findByOrderIdAndProductId(
                        returnItem.getReturnRequest().getOrderId(),
                        returnItem.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        orderDetail.setStatus(OrderDetailStatus.RETURNED);
        orderDetailsRepository.save(orderDetail);

        // Check if all items are processed
        checkAndUpdateRequestStatus(returnItem.getReturnRequest());
    }

    public void rejectReturnItem(Long returnRequestId, Long itemId, String rejectionReason) {
        ReturnItem returnItem = (ReturnItem) returnItemRepository.findByIdAndReturnRequestId(itemId, returnRequestId)
                .orElseThrow(() -> new RuntimeException("Return item not found"));

        if (returnItem.getStatus() != ReturnItemStatus.PENDING) {
            throw new RuntimeException("Return item already processed");
        }

        // Update return item status
        returnItem.setStatus(ReturnItemStatus.REJECTED);
        returnItem.setRejectionReason(rejectionReason);
        returnItem.setProcessedDate(LocalDateTime.now());
        returnItemRepository.save(returnItem);

        // Revert order item status
        OrderDetails orderDetail = orderDetailsRepository.findByOrderIdAndProductId(
                        returnItem.getReturnRequest().getOrderId(),
                        returnItem.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        orderDetail.setStatus(OrderDetailStatus.ACTIVE);
        orderDetailsRepository.save(orderDetail);

        // Check if all items are processed
        checkAndUpdateRequestStatus(returnItem.getReturnRequest());
    }

    private void checkAndUpdateRequestStatus(ReturnRequest returnRequest) {
        List<ReturnItem> items = returnRequest.getItems();

        boolean allApproved = items.stream().allMatch(i -> i.getStatus() == ReturnItemStatus.APPROVED);
        boolean allRejected = items.stream().allMatch(i -> i.getStatus() == ReturnItemStatus.REJECTED);
        boolean anyPending = items.stream().anyMatch(i -> i.getStatus() == ReturnItemStatus.PENDING);

        ReturnStatus newStatus;
        if (allApproved) {
            newStatus = ReturnStatus.APPROVED;
            // Process refund if all items approved
            processRefund(returnRequest);
        } else if (allRejected) {
            newStatus = ReturnStatus.REJECTED;
        } else if (anyPending) {
            newStatus = ReturnStatus.PARTIALLY_PROCESSED;
        } else {
            newStatus = ReturnStatus.PARTIALLY_PROCESSED;
        }

        if (newStatus != returnRequest.getStatus()) {
            returnRequest.setStatus(newStatus);
            returnRequestRepository.save(returnRequest);
        }

        // Update order status if all items are returned
        if (allApproved) {
            Order order = orderRepository.findById(returnRequest.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (order.getOrderDetails().stream()
                    .allMatch(od -> od.getStatus() == OrderDetailStatus.RETURNED)) {
                order.setStatus(OrderStatus.RETURNED);
                orderRepository.save(order);
            }
        }
    }

    private void processRefund(ReturnRequest returnRequest) {
        // Implement your refund logic here
        // This could involve:
        // 1. Calculating total refund amount
        // 2. Processing payment gateway refund
        // 3. Updating payment records
    }

    public List<ReturnRequest> getUserReturnRequests(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return requests.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        return returnRequestRepository.findByUserId(Long.valueOf(user.getId()));
    }

    public ReturnRequest getReturnRequest(Long id, Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return request.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        return returnRequestRepository.findByIdAndUserId(id, Long.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Return request not found"));
    }



    public List<ReturnRequest> getAllReturnRequest(){
        return returnRequestRepository.findAll();
    }


















    public ReturnRequest updateReturnStatus(Long returnId, Long itemId, String newStatus, String adminUsername) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found"));

        ReturnItem item = returnRequest.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        // Create new history entry
        StatusHistory history = new StatusHistory();
        history.setStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy(adminUsername);

        // Update current status and history
        item.setCurrentStatus(newStatus);
item.setStatus(ReturnItemStatus.valueOf(newStatus));
        item.getStatusHistory().add(history);

        return returnRequestRepository.save(returnRequest);
    }








    public List<ReturnRequest> getReturnsByStatus(String status) {
        return returnRequestRepository.findByItemsCurrentStatus(status);
    }










    public List<ReturnRequest> getAllReturns() {
        return returnRequestRepository.findAll();
    }

    public List<ReturnItem> getAllReturnItem() {
        return returnItemRepository.findAll();
    }


    public List<ReturnRequest> getAllReturn() {
        return returnRequestRepository.findAll();
    }










    public List<MonthlyReturnSummary> getMonthlyReturns(int year) {
        return returnRequestRepository.getMonthlyReturns(year);
    }















    //GET RETURN REQUEST BASED ON A FARMER


    public List<ReturnRequestDTO> getReturnsForCurrentFarmer(Long farmerId) {
//        User currentFarmer = authenticationService.getCurrentUser();



        List<ReturnRequest> returns = returnRequestRepository.findByFarmerId(farmerId);

        System.out.println(returns.toArray());
        return returns.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReturnRequestDTO convertToDTO(ReturnRequest returnRequest) {
        ReturnRequestDTO dto = new ReturnRequestDTO();
        dto.setId(returnRequest.getId());
        dto.setOrderId(returnRequest.getOrderId());
        dto.setReason(returnRequest.getReason());
        dto.setCreatedAt(returnRequest.getCreatedAt());
        dto.setRequestDate(returnRequest.getRequestDate());
        dto.setStatus(returnRequest.getStatus());
        dto.setItems(convertItemsToDTO(returnRequest.getItems()));
        return dto;
    }

    private List<ReturnItemDTO> convertItemsToDTO(List<ReturnItem> items) {
        return items.stream()
                .map(item -> ReturnItemDTO.builder()
                        .id(item.getId())
                        .id(item.getProduct().getId())
                        //.productId(item.getProductId())
                        .name(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .reason(item.getReason())
                        .image(String.valueOf(item.getProduct().getImageUrls()))
                        .build())
                .collect(Collectors.toList());
    }
    // For paginated results
    public Page<ReturnRequestDTO> getReturnsForCurrentFarmer(Pageable pageable, Long farmerId) {
//        User currentFarmer = authenticationService.getCurrentUser();
        Page<ReturnRequest> returns = (Page<ReturnRequest>) returnRequestRepository.findByFarmerId(farmerId);
        return returns.map(this::convertToDTO);
    }





}