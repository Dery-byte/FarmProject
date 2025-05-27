package com.alibou.book.Controllers;

import com.alibou.book.DTO.*;
import com.alibou.book.Entity.ReturnItem;
import com.alibou.book.Entity.ReturnRequest;
import com.alibou.book.Services.ReturnService;
import com.alibou.book.user.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/return")
public class ReturnController {
    private final ReturnService returnService;
    private final UserDetailsService userDetailsService;

    private final ModelMapper modelMapper;

    public ReturnController(ReturnService returnService, UserDetailsService userDetailsService, ModelMapper modelMapper) {
        this.returnService = returnService;
        this.userDetailsService = userDetailsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/request")
    public ResponseEntity<ReturnResponse> createReturnRequest(
            @RequestBody ReturnRequestDTO returnRequestDTO,
            Principal principal) {

        ReturnRequest returnRequest = returnService.createReturnRequest(returnRequestDTO, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(returnRequest));
    }

    @GetMapping("/requestByUser")
    public ResponseEntity<List<ReturnResponse>> getUserReturnRequests(Principal principal) {
        List<ReturnRequest> returnRequests = returnService.getUserReturnRequests(principal);
        List<ReturnResponse> responses = returnRequests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/byRequestId/{id}")
    public ResponseEntity<ReturnResponse> getReturnRequest(@PathVariable Long id, Principal principal) {
        ReturnRequest returnRequest = returnService.getReturnRequest(id, principal);
        return ResponseEntity.ok(toResponse(returnRequest));
    }

    private ReturnResponse toResponse(ReturnRequest returnRequest) {
        ReturnResponse response = modelMapper.map(returnRequest, ReturnResponse.class);
        response.setStatus(returnRequest.getStatus().name());

        List<ReturnItemResponse> itemResponses = returnRequest.getItems().stream()
                .map(item -> modelMapper.map(item, ReturnItemResponse.class))
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }



    @GetMapping("/requestUser")
    public ResponseEntity<ReturnResponse> getReturnRequest(Principal principal) {
        ReturnRequest returnRequest = (ReturnRequest) returnService.getUserReturnRequests(principal);
        return ResponseEntity.ok(toResponse(returnRequest));
    }





















    @PatchMapping("/{returnId}/items/{itemId}/status")
    public ResponseEntity<ReturnRequest> updateStatus(
            @PathVariable Long returnId,
            @PathVariable Long itemId,
            @RequestBody StatusUpdateRequest request,
            Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to view return request.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        ReturnRequest updated = returnService.updateReturnStatus(
                returnId,
                itemId,
                request.getNewStatus(),
                user.getFullName()
        );
        return ResponseEntity.ok(updated);
    }





    @GetMapping("returnByStatus")
    public ResponseEntity<List<ReturnRequest>> getReturnsByStatus(
            @RequestParam(required = false) String status) {

        List<ReturnRequest> returns = status != null
                ? returnService.getReturnsByStatus(status)
                : returnService.getAllReturns();
        return ResponseEntity.ok(returns);
    }




    @GetMapping("getAllReturnItems")
    public List<ReturnItem> getAllRequest(){
        return returnService.getAllReturnItem();
    }

    @GetMapping("getAllReturnReuests")
    public List<ReturnRequest> getAllReturn() {
        return returnService.getAllReturn();
    }




    @GetMapping("/monthlyReturns")
    public ResponseEntity<List<MonthlyReturnSummary>> getMonthlyReturns(@RequestParam int year) {
        List<MonthlyReturnSummary> data = returnService.getMonthlyReturns(year);
        return ResponseEntity.ok(data);
    }




//    @GetMapping("/monthlyReturns")
//    public ResponseEntity<List<MonthlyReturnSummary>> getMonthlyReturns(
//            @RequestParam(required = false) Integer year) {
//
//        if (year == null) {
//            year = LocalDate.now().getYear();
//        }
//
//        List<MonthlyReturnSummary> data = returnService.getMonthlyReturns(year);
//        return ResponseEntity.ok(data);
//    }






















































    //GET RETUNRED ITEMS FOR A PARTICULAR FARMER



    @GetMapping("/allRturnsByFarmer")
    public ResponseEntity<List<ReturnRequestDTO>> getFarmerReturns(Principal principal) {

                if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        List<ReturnRequestDTO> returns = returnService.getReturnsForCurrentFarmer(Long.valueOf(user.getId()));
        return ResponseEntity.ok(returns);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ReturnRequestDTO>> getFarmerReturnsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Principal principal) {

        // Get current farmer ID
        if (principal == null) {
            throw new IllegalArgumentException("User must be authenticated to fetch orders.");
        }
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        // Get paginated results
        Page<ReturnRequestDTO> returns = (Page<ReturnRequestDTO>) returnService.getReturnsForCurrentFarmer(Long.valueOf(user.getId()));

        return ResponseEntity.ok(returns);
    }

}