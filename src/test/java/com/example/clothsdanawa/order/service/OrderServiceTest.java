// package com.example.clothsdanawa.order.service;
//
// import com.example.clothsdanawa.cart.entity.Cart;
// import com.example.clothsdanawa.cart.entity.CartItem;
// import com.example.clothsdanawa.cart.repository.CartRepository;
// import com.example.clothsdanawa.cart.service.CartService;
// import com.example.clothsdanawa.common.exception.BaseException;
// import com.example.clothsdanawa.common.exception.ErrorCode;
// import com.example.clothsdanawa.order.dto.OrderRequestDto;
// import com.example.clothsdanawa.order.dto.OrderResponseDto;
// import com.example.clothsdanawa.order.entity.Order;
// import com.example.clothsdanawa.order.entity.OrderStatus;
// import com.example.clothsdanawa.order.repository.OrderRepository;
// import com.example.clothsdanawa.product.entity.Product;
// import com.example.clothsdanawa.product.service.ProductService;
// import com.example.clothsdanawa.user.entity.User;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.*;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import java.util.List;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.BDDMockito.*;
//
// @ExtendWith(MockitoExtension.class)
// class OrderServiceTest {
//
// 	@Mock private OrderRepository orderRepository;
// 	@Mock private CartRepository cartRepository;
// 	@Mock private ProductService productService;
// 	@Mock private CartService cartService;
// 	@InjectMocks private OrderService orderService;
//
// 	@Test
// 	void 주문_생성에_성공한다() {
// 		// given
// 		Long cartId = 1L;
//
// 		Product product = TestEntityFactory.createProduct(1L, "반팔", 20000, 10);
// 		User user = TestEntityFactory.createUser(1L, "user@example.com");
//
// 		// CartItem 생성
// 		CartItem cartItem = new CartItem();
// 		ReflectionTestUtils.setField(cartItem, "product", product);
// 		ReflectionTestUtils.setField(cartItem, "quantity", 1);
//
// 		// Cart 생성
// 		Cart cart = new Cart();
// 		ReflectionTestUtils.setField(cart, "user", user);
// 		ReflectionTestUtils.setField(cart, "cartItems", List.of(cartItem));
// 		ReflectionTestUtils.setField(cart, "id", cartId);
// 		ReflectionTestUtils.setField(cart, "totalPrice", 10000);
//
// 		// cartItem에 cart도 설정 (연관관계 유지)
// 		ReflectionTestUtils.setField(cartItem, "cart", cart);
//
// 		OrderRequestDto request = new OrderRequestDto(12000); // 포인트 12000원 사용
//
// 		given(orderRepository.findOrderByCartId(cartId)).willReturn(Optional.empty());
// 		given(cartRepository.findById(cartId)).willReturn(Optional.of(cart));
// 		given(orderRepository.save(any(Order.class))).willAnswer(inv -> inv.getArgument(0));
//
// 		// when
// 		OrderResponseDto response = orderService.createOrder(cartId, request);
//
// 		// then
// 		assertNotNull(response);
// 		assertEquals(OrderStatus.WAITING, response.getOrderStatus());
// 		verify(productService).decreaseStock(product.getId(), cartItem.getQuantity());
// 		verify(cartService).deleteCart(user.getUserId());
// 	}
//
//
// 	@Test
// 	void 이미_주문이_존재하면_예외를_던진다() {
// 		// given
// 		Long cartId = 1L;
// 		given(orderRepository.findOrderByCartId(cartId)).willReturn(Optional.of(new Order()));
//
// 		// when & then
// 		BaseException exception = assertThrows(BaseException.class, () ->
// 			orderService.createOrder(cartId, new OrderRequestDto(10000))
// 		);
//
// 		assertEquals(ErrorCode.ORDER_DUPLICATE_CREATION, exception.getErrorCode());
// 	}
//
// 	@Test
// 	void 주문_조회에_성공한다() {
// 		// given
// 		Long orderId = 1L;
// 		Order order = mock(Order.class);
// 		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
//
// 		// when
// 		OrderResponseDto response = orderService.findOrder(orderId);
//
// 		// then
// 		assertNotNull(response);
// 	}
//
// 	@Test
// 	void 존재하지_않는_주문을_조회하면_예외가_발생한다() {
// 		// given
// 		given(orderRepository.findById(anyLong())).willReturn(Optional.empty());
//
// 		// when & then
// 		assertThrows(BaseException.class, () -> orderService.findOrder(1L));
// 	}
//
// 	@Test
// 	void 주문상태를_변경하면_메시지를_반환한다() {
// 		// given
// 		Long orderId = 1L;
// 		Order order = mock(Order.class);
// 		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
//
// 		// when
// 		String result = orderService.updateStatus(orderId, OrderStatus.COMPLETED);
//
// 		// then
// 		verify(order).updateStatus(OrderStatus.COMPLETED);
// 		assertEquals("배송이 완료되었습니다.", result);
// 	}
//
// 	@Test
// 	void 주문을_정상적으로_취소한다() {
// 		// given
// 		Long orderId = 1L;
// 		Order order = mock(Order.class);
// 		given(order.getOrderStatus()).willReturn(OrderStatus.WAITING);
// 		given(order.getTotalPrice()).willReturn(10000);
// 		given(order.getPoint()).willReturn(2000);
// 		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
//
// 		// when
// 		String result = orderService.cancelOrder(orderId);
//
// 		// then
// 		verify(order).updatePoint(12000);
// 		verify(order).updateStatus(OrderStatus.CANCELLED);
// 		assertEquals("주문이 취소되었습니다.", result);
// 	}
//
// 	@Test
// 	void 이미_취소된_주문이면_예외가_발생한다() {
// 		// given
// 		Order order = mock(Order.class);
// 		given(order.getOrderStatus()).willReturn(OrderStatus.CANCELLED);
// 		given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
//
// 		// when & then
// 		BaseException exception = assertThrows(BaseException.class, () -> orderService.cancelOrder(1L));
// 		assertEquals(ErrorCode.ALREADY_CANCELLED, exception.getErrorCode());
// 	}
//
// 	@Test
// 	void 배송_완료된_주문은_취소할_수_없다() {
// 		// given
// 		Order order = mock(Order.class);
// 		given(order.getOrderStatus()).willReturn(OrderStatus.COMPLETED);
// 		given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));
//
// 		// when & then
// 		BaseException exception = assertThrows(BaseException.class, () -> orderService.cancelOrder(1L));
// 		assertEquals(ErrorCode.ALREADY_DELIVERED, exception.getErrorCode());
// 	}
// 	public class TestEntityFactory {
// 		public static User createUser(Long id, String email) {
// 			User user = mock(User.class);
// 			given(user.getUserId()).willReturn(id);
// 			return user;
// 		}
//
// 		public static Product createProduct(Long id, String name, int price, int stock) {
// 			Product product = mock(Product.class);
// 			given(product.getId()).willReturn(id);
// 			given(product.getProductName()).willReturn(name);
// 			given(product.getPrice()).willReturn(price);
// 			return product;
// 		}
// 	}
//
// }
