package com.alibou.book.DTO;

import lombok.Data;

import java.util.List;

@Data
public class BulkUpdateCartRequest {
    private List<UpdateCartItemRequest> items;
}
