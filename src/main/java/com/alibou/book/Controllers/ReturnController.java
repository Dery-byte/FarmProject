package com.alibou.book.Controllers;

import com.alibou.book.DTO.ReturnItemResponse;
import com.alibou.book.DTO.ReturnRequestDTO;
import com.alibou.book.DTO.ReturnResponse;
import com.alibou.book.Entity.ReturnRequest;
import com.alibou.book.Services.ReturnService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/return")
public class ReturnController {
    private final ReturnService returnService;
    private final ModelMapper modelMapper;

    public ReturnController(ReturnService returnService, ModelMapper modelMapper) {
        this.returnService = returnService;
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







}