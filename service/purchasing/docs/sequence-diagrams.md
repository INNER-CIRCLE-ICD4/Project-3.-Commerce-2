# 시퀀스 다이어그램

## 1. 장바구니 생성 (CreateCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant CreateCartUseCase
    participant Cart
    participant CartRepositoryPort
    participant CartRepositoryAdapter
    participant CartRepository
    participant CartJpaRepository
    participant DB

    Client->>CartController: POST /api/v1/carts
    CartController->>CreateCartUseCase: execute(command)
    CreateCartUseCase->>Cart: new Cart(cartId, customerId, timeProvider)
    Cart-->>CreateCartUseCase: cart instance
    CreateCartUseCase->>CartRepositoryPort: save(cart)
    Note over CartRepositoryPort: Interface (Port)
    CartRepositoryPort->>CartRepositoryAdapter: save(cart)
    Note over CartRepositoryAdapter: Adapter implementation
    CartRepositoryAdapter->>CartRepository: save(cart)
    Note over CartRepository: Internal JPA handler
    CartRepository->>CartRepository: toEntity(cart)
    CartRepository->>CartJpaRepository: save(entity)
    CartJpaRepository->>DB: INSERT
    DB-->>CartJpaRepository: OK
    CartJpaRepository-->>CartRepository: saved entity
    CartRepository->>CartRepository: toDomain(entity)
    CartRepository-->>CartRepositoryAdapter: saved cart
    CartRepositoryAdapter-->>CartRepositoryPort: saved cart
    CartRepositoryPort-->>CreateCartUseCase: saved cart
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
    participant CartRepositoryPort
    participant ProductPriceProvider
    participant ProductServiceRestClient
    participant ProductService
    participant InventoryChecker
    participant Cart
    participant DB

    Client->>CartController: POST /api/v1/carts/{cartId}/items
    CartController->>AddItemToCartUseCase: execute(command)
    
    %% 장바구니 조회
    AddItemToCartUseCase->>CartRepositoryPort: findById(cartId)
    CartRepositoryPort->>DB: SELECT
    DB-->>CartRepositoryPort: cart data
    CartRepositoryPort-->>AddItemToCartUseCase: Cart
    
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
    AddItemToCartUseCase->>CartRepositoryPort: save(cart)
    CartRepositoryPort->>DB: UPDATE
    DB-->>CartRepositoryPort: OK
    CartRepositoryPort-->>AddItemToCartUseCase: saved cart
    
    AddItemToCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 3. 장바구니 상품 수량 변경 (UpdateCartItemQuantityUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant UpdateCartItemQuantityUseCase
    participant CartRepositoryPort
    participant InventoryChecker
    participant ProductServiceRestClient
    participant Cart
    participant CartItem
    participant DB

    Client->>CartController: PUT /api/v1/carts/{cartId}/items/{itemId}
    CartController->>UpdateCartItemQuantityUseCase: execute(command)
    
    %% 장바구니 조회
    UpdateCartItemQuantityUseCase->>CartRepositoryPort: findById(cartId)
    CartRepositoryPort->>DB: SELECT
    DB-->>CartRepositoryPort: cart data
    CartRepositoryPort-->>UpdateCartItemQuantityUseCase: Cart
    
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
    UpdateCartItemQuantityUseCase->>CartRepositoryPort: save(cart)
    CartRepositoryPort->>DB: UPDATE
    DB-->>CartRepositoryPort: OK
    CartRepositoryPort-->>UpdateCartItemQuantityUseCase: saved cart
    
    UpdateCartItemQuantityUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 4. 장바구니 조회 (GetCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant GetCartUseCase
    participant CartRepositoryPort
    participant ProductQueryService
    participant ProductServiceRestClient
    participant ProductService
    participant DB

    Client->>CartController: GET /api/v1/carts/{cartId}
    CartController->>GetCartUseCase: execute(cartId)
    
    %% 장바구니 조회
    GetCartUseCase->>CartRepositoryPort: findById(cartId)
    CartRepositoryPort->>DB: SELECT
    DB-->>CartRepositoryPort: cart data
    CartRepositoryPort-->>GetCartUseCase: Cart
    
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
    participant CartRepositoryPort
    participant Cart
    participant DB

    Client->>CartController: DELETE /api/v1/carts/{cartId}/items/{itemId}
    CartController->>RemoveItemFromCartUseCase: execute(command)
    
    %% 장바구니 조회
    RemoveItemFromCartUseCase->>CartRepositoryPort: findById(cartId)
    CartRepositoryPort->>DB: SELECT
    DB-->>CartRepositoryPort: cart data
    CartRepositoryPort-->>RemoveItemFromCartUseCase: Cart
    
    %% 아이템 제거
    RemoveItemFromCartUseCase->>Cart: removeItem(itemId)
    Cart->>Cart: findAndRemoveItem(itemId)
    Cart-->>RemoveItemFromCartUseCase: updated cart
    
    %% 저장
    RemoveItemFromCartUseCase->>CartRepositoryPort: save(cart)
    CartRepositoryPort->>DB: UPDATE/DELETE
    DB-->>CartRepositoryPort: OK
    CartRepositoryPort-->>RemoveItemFromCartUseCase: saved cart
    
    RemoveItemFromCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 6. 장바구니 병합 (MergeCartsUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant MergeCartsUseCase
    participant CartRepositoryPort
    participant Cart
    participant DB

    Client->>CartController: POST /api/v1/carts/merge
    CartController->>MergeCartsUseCase: execute(command)
    
    %% 소스 장바구니 조회
    MergeCartsUseCase->>CartRepositoryPort: findById(sourceCartId)
    CartRepositoryPort->>DB: SELECT source cart
    DB-->>CartRepositoryPort: source cart data
    CartRepositoryPort-->>MergeCartsUseCase: Source Cart
    
    %% 타겟 장바구니 조회
    MergeCartsUseCase->>CartRepositoryPort: findById(targetCartId)
    CartRepositoryPort->>DB: SELECT target cart
    DB-->>CartRepositoryPort: target cart data
    CartRepositoryPort-->>MergeCartsUseCase: Target Cart
    
    %% 병합
    MergeCartsUseCase->>Cart: mergeFrom(sourceCart)
    Cart->>Cart: validateAndMergeItems()
    Cart-->>MergeCartsUseCase: merged cart
    
    %% 타겟 장바구니 저장
    MergeCartsUseCase->>CartRepositoryPort: save(targetCart)
    CartRepositoryPort->>DB: UPDATE target cart
    DB-->>CartRepositoryPort: OK
    
    %% 소스 장바구니 삭제
    MergeCartsUseCase->>CartRepositoryPort: deleteById(sourceCartId)
    CartRepositoryPort->>DB: DELETE source cart
    DB-->>CartRepositoryPort: OK
    
    MergeCartsUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```

## 7. 장바구니 비우기 (ClearCartUseCase)

```mermaid
sequenceDiagram
    participant Client
    participant CartController
    participant ClearCartUseCase
    participant CartRepositoryPort
    participant Cart
    participant DB

    Client->>CartController: DELETE /api/v1/carts/{cartId}/items
    CartController->>ClearCartUseCase: execute(command)
    
    %% 장바구니 조회
    ClearCartUseCase->>CartRepositoryPort: findById(cartId)
    CartRepositoryPort->>DB: SELECT
    DB-->>CartRepositoryPort: cart data
    CartRepositoryPort-->>ClearCartUseCase: Cart
    
    %% 모든 아이템 제거
    ClearCartUseCase->>Cart: clear()
    Cart->>Cart: removeAllItems()
    Cart-->>ClearCartUseCase: cleared cart
    
    %% 저장
    ClearCartUseCase->>CartRepositoryPort: save(cart)
    CartRepositoryPort->>DB: UPDATE (remove all items)
    DB-->>CartRepositoryPort: OK
    CartRepositoryPort-->>ClearCartUseCase: saved cart
    
    ClearCartUseCase-->>CartController: CartResult
    CartController-->>Client: 200 OK
```