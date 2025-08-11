package org.icd4.commerce.adapter.webapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.webapi.dto.ApiResponse;
import org.icd4.commerce.adapter.webapi.dto.StockRegisterRequest;
import org.icd4.commerce.adapter.webapi.dto.StockResponse;
import org.icd4.commerce.adapter.webapi.dto.StockUpdateRequest;
import org.icd4.commerce.application.StockService;
import org.icd4.commerce.domain.Stock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockApi {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> registerStock(@Valid @RequestBody StockRegisterRequest request) {
            Stock registeredStock = stockService.register(request.getProductId(), request.getQuantity());

            StockResponse response = StockResponse.from(registeredStock);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("재고가 성공적으로 등록되었습니다.", response));
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<ApiResponse<StockResponse>> getStock(@PathVariable String stockId) {
        Stock stock = stockService.getStock(stockId);

        StockResponse response = StockResponse.from(stock);
        
        return ResponseEntity.ok()
                .body(ApiResponse.success("재고 조회 성공", response));
    }

    @GetMapping("/{stockId}/quantity")
    public ResponseEntity<ApiResponse<Long>> getStockQuantity(@PathVariable String stockId) {
        Long quantity = stockService.checkQuantity(stockId);

        return ResponseEntity.ok()
                .body(ApiResponse.success("재고 수량 조회 성공", quantity));
    }

    @PatchMapping("/{stockId}/increase")
    public ResponseEntity<ApiResponse<Long>> increaseStock(@PathVariable String stockId,
                                                           @Valid @RequestBody StockUpdateRequest request) {
        Long increaseQuantity = stockService.increaseQuantity(stockId, request.getQuantity());

        return ResponseEntity.ok()
                .body(ApiResponse.success("재고가 성공적으로 증가되었습니다.", increaseQuantity));
    }

    @PatchMapping("/{stockId}/decrease")
    public ResponseEntity<ApiResponse<Long>> decreaseStock(@PathVariable String stockId,
                                                           @Valid @RequestBody StockUpdateRequest request) {
        Long decreaseQuantity = stockService.decreaseQuantity(stockId, request.getQuantity());

        return ResponseEntity.ok()
                    .body(ApiResponse.success("재고가 성공적으로 감소되었습니다.", decreaseQuantity));
    }

    @PatchMapping("/v1/{stockId}/increase")
    public ResponseEntity<ApiResponse<Integer>> increaseStockV1(@PathVariable String stockId,
                                                           @Valid @RequestBody StockUpdateRequest request) {
        Integer increaseQuantity = stockService.increaseQuantityV1(stockId, request.getQuantity());

        return ResponseEntity.ok()
                .body(ApiResponse.success("재고가 성공적으로 증가되었습니다.", increaseQuantity));
    }

    @PatchMapping("/v1/{stockId}/v1/decrease")
    public ResponseEntity<ApiResponse<Integer>> decreaseStockV1(@PathVariable String stockId,
                                                           @Valid @RequestBody StockUpdateRequest request) {
        Integer decreaseQuantity = stockService.decreaseQuantityV1(stockId, request.getQuantity());

        return ResponseEntity.ok()
                .body(ApiResponse.success("재고가 성공적으로 감소되었습니다.", decreaseQuantity));
    }
}
