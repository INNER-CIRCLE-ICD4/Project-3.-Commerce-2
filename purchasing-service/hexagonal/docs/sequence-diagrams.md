# 시퀀스 다이어그램

## 1. 장바구니 생성 (CreateCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant CreateCartUseCase
    participant Cart
    participant CartRepository
    participant CartRepositoryAdapter
    participant CartJpaRepository
    participant DB

    Client->>CartController: POST /api/v1/carts
    CartController->>CreateCartUseCase: execute(command)
    CreateCartUseCase->>Cart: new Cart(cartId, customerId, timeProvider)
    Cart-->>CreateCartUseCase: cart instance
    CreateCartUseCase->>CartRepository: save(cart)
    CartRepository->>CartRepositoryAdapter: save(cart)
    CartRepositoryAdapter->>CartRepositoryAdapter: toEntity(cart)
    CartRepositoryAdapter->>CartJpaRepository: save(entity)
    CartJpaRepository->>DB: INSERT
    DB-->>CartJpaRepository: OK
    CartJpaRepository-->>CartRepositoryAdapter: saved entity
    CartRepositoryAdapter->>CartRepositoryAdapter: toDomain(entity)
    CartRepositoryAdapter-->>CartRepository: saved cart
    CartRepository-->>CreateCartUseCase: saved cart
    CreateCartUseCase->>CreateCartUseCase: CartResult.from(cart)
    CreateCartUseCase-->>CartController: CartResult
    CartController-->>Client: 201 Created
```

## 2. 장바구니에 상품 추가 (AddItemToCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant AddItemToCartUseCase
    participant CartRepository
    participant ProductPriceProvider
    participant ProductServiceRestClient
    participant ProductService
    participant InventoryChecker
    participant Cart
    participant DB

    Client->>CartController: POST /api/v1/carts/{cartId}/items
    CartController->>AddItemToCartUseCase: execute(command)
    
    %% 장바구니 조회
    AddItemToCartUseCase->>CartRepository: findById(cartId)
    CartRepository->>DB: SELECT
    DB-->>CartRepository: cart data
    CartRepository-->>AddItemToCartUseCase: Cart
    
    %% 상품 가격 조회
    AddItemToCartUseCase->>ProductPriceProvider: getPrice(productId)
    ProductPriceProvider->>ProductServiceRestClient: getProduct(productId)
    ProductServiceRestClient->>ProductService: GET /api/v1/products/{id}
    ProductService-->>ProductServiceRestClient: product info
    ProductServiceRestClient-->>ProductPriceProvider: ProductInfo
    ProductPriceProvider-->>AddItemToCartUseCase: price
    
    %% 재고 확인
    AddItemToCartUseCase->>InventoryChecker: hasStock(productId, quantity)
    InventoryChecker->>ProductServiceRestClient: getAvailableStock(productId)
    ProductServiceRestClient->>ProductService: GET /api/v1/products/{id}/stock
    ProductService-->>ProductServiceRestClient: stock info
    ProductServiceRestClient-->>InventoryChecker: available quantity
    InventoryChecker-->>AddItemToCartUseCase: true/false
    
    %% 장바구니에 아이템 추가
    AddItemToCartUseCase->>Cart: addItem(productId, options, quantity, price)
    Cart->>Cart: validateAndAddItem()
    Cart-->>AddItemToCartUseCase: updated cart
    
    %% 저장
    AddItemToCartUseCase->>CartRepository: save(cart)
    CartRepository->>DB: UPDATE
    DB-->>CartRepository: OK
    CartRepository-->>AddItemToCartUseCase: saved cart
    
    AddItemToCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 3. 장바구니 상품 수량 변경 (UpdateCartItemQuantityUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant UpdateCartItemQuantityUseCase
    participant CartRepository
    participant InventoryChecker
    participant ProductServiceRestClient
    participant Cart
    participant CartItem
    participant DB

    Client->>CartController: PUT /api/v1/carts/{cartId}/items/{itemId}
    CartController->>UpdateCartItemQuantityUseCase: execute(command)
    
    %% 장바구니 조회
    UpdateCartItemQuantityUseCase->>CartRepository: findById(cartId)
    CartRepository->>DB: SELECT
    DB-->>CartRepository: cart data
    CartRepository-->>UpdateCartItemQuantityUseCase: Cart
    
    %% 재고 확인
    UpdateCartItemQuantityUseCase->>InventoryChecker: hasStock(productId, newQuantity)
    InventoryChecker->>ProductServiceRestClient: getAvailableStock(productId)
    ProductServiceRestClient-->>InventoryChecker: available quantity
    InventoryChecker-->>UpdateCartItemQuantityUseCase: true/false
    
    %% 수량 변경
    UpdateCartItemQuantityUseCase->>Cart: updateItemQuantity(itemId, newQuantity)
    Cart->>Cart: findItem(itemId)
    Cart->>CartItem: updateQuantity(newQuantity)
    CartItem-->>Cart: updated
    Cart-->>UpdateCartItemQuantityUseCase: updated cart
    
    %% 저장
    UpdateCartItemQuantityUseCase->>CartRepository: save(cart)
    CartRepository->>DB: UPDATE
    DB-->>CartRepository: OK
    CartRepository-->>UpdateCartItemQuantityUseCase: saved cart
    
    UpdateCartItemQuantityUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 4. 장바구니 조회 (GetCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant GetCartUseCase
    participant CartRepository
    participant ProductQueryService
    participant ProductServiceRestClient
    participant ProductService
    participant DB

    Client->>CartController: GET /api/v1/carts/{cartId}
    CartController->>GetCartUseCase: execute(cartId)
    
    %% 장바구니 조회
    GetCartUseCase->>CartRepository: findById(cartId)
    CartRepository->>DB: SELECT
    DB-->>CartRepository: cart data
    CartRepository-->>GetCartUseCase: Cart
    
    %% 상품 정보 배치 조회 (선택적)
    GetCartUseCase->>GetCartUseCase: extractProductIds(cart)
    GetCartUseCase->>ProductQueryService: getProducts(productIds)
    ProductQueryService->>ProductServiceRestClient: getProductsBatch(productIds)
    ProductServiceRestClient->>ProductService: POST /api/v1/products/batch
    ProductService-->>ProductServiceRestClient: products info
    ProductServiceRestClient-->>ProductQueryService: List<ProductInfo>
    ProductQueryService-->>GetCartUseCase: Map<ProductId, ProductInfo>
    
    GetCartUseCase->>GetCartUseCase: CartResult.from(cart, productInfos)
    GetCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 5. 장바구니 상품 제거 (RemoveItemFromCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant RemoveItemFromCartUseCase
    participant CartRepository
    participant Cart
    participant DB

    Client->>CartController: DELETE /api/v1/carts/{cartId}/items/{itemId}
    CartController->>RemoveItemFromCartUseCase: execute(command)
    
    %% 장바구니 조회
    RemoveItemFromCartUseCase->>CartRepository: findById(cartId)
    CartRepository->>DB: SELECT
    DB-->>CartRepository: cart data
    CartRepository-->>RemoveItemFromCartUseCase: Cart
    
    %% 아이템 제거
    RemoveItemFromCartUseCase->>Cart: removeItem(itemId)
    Cart->>Cart: findAndRemoveItem(itemId)
    Cart-->>RemoveItemFromCartUseCase: updated cart
    
    %% 저장
    RemoveItemFromCartUseCase->>CartRepository: save(cart)
    CartRepository->>DB: UPDATE/DELETE
    DB-->>CartRepository: OK
    CartRepository-->>RemoveItemFromCartUseCase: saved cart
    
    RemoveItemFromCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 6. 장바구니 병합 (MergeCartsUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant MergeCartsUseCase
    participant CartRepository
    participant Cart
    participant DB

    Client->>CartController: POST /api/v1/carts/merge
    CartController->>MergeCartsUseCase: execute(command)
    
    %% 소스 장바구니 조회
    MergeCartsUseCase->>CartRepository: findById(sourceCartId)
    CartRepository->>DB: SELECT source cart
    DB-->>CartRepository: source cart data
    CartRepository-->>MergeCartsUseCase: Source Cart
    
    %% 타겟 장바구니 조회
    MergeCartsUseCase->>CartRepository: findById(targetCartId)
    CartRepository->>DB: SELECT target cart
    DB-->>CartRepository: target cart data
    CartRepository-->>MergeCartsUseCase: Target Cart
    
    %% 병합
    MergeCartsUseCase->>Cart: mergeFrom(sourceCart)
    Cart->>Cart: validateAndMergeItems()
    Cart-->>MergeCartsUseCase: merged cart
    
    %% 타겟 장바구니 저장
    MergeCartsUseCase->>CartRepository: save(targetCart)
    CartRepository->>DB: UPDATE target cart
    DB-->>CartRepository: OK
    
    %% 소스 장바구니 삭제
    MergeCartsUseCase->>CartRepository: delete(sourceCartId)
    CartRepository->>DB: DELETE source cart
    DB-->>CartRepository: OK
    
    MergeCartsUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 7. 장바구니 비우기 (ClearCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant ClearCartUseCase
    participant CartRepository
    participant Cart
    participant DB

    Client->>CartController: DELETE /api/v1/carts/{cartId}/items
    CartController->>ClearCartUseCase: execute(command)
    
    %% 장바구니 조회
    ClearCartUseCase->>CartRepository: findById(cartId)
    CartRepository->>DB: SELECT
    DB-->>CartRepository: cart data
    CartRepository-->>ClearCartUseCase: Cart
    
    %% 모든 아이템 제거
    ClearCartUseCase->>Cart: clear()
    Cart->>Cart: removeAllItems()
    Cart-->>ClearCartUseCase: cleared cart
    
    %% 저장
    ClearCartUseCase->>CartRepository: save(cart)
    CartRepository->>DB: UPDATE (remove all items)
    DB-->>CartRepository: OK
    CartRepository-->>ClearCartUseCase: saved cart
    
    ClearCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```