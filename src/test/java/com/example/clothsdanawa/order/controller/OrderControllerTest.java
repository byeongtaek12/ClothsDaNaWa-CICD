// package com.example.clothsdanawa.order.controller;
//
// import com.example.clothsdanawa.order.dto.OrderRequestDto;
// import com.example.clothsdanawa.order.dto.OrderResponseDto;
// import com.example.clothsdanawa.order.entity.OrderStatus;
// import com.example.clothsdanawa.order.service.OrderService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentMatchers;
//
// import java.util.Map;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
// import static org.assertj.core.api.Assertions.assertThat;
//
// public class OrderControllerTest {
//
// 	private OrderService orderService;
// 	private OrderController orderController;
//
// 	@BeforeEach
// 	void setUp() {
// 		orderService = mock(OrderService.class);
// 		orderController = new OrderController(orderService);
// 	}
//
// 	@Test
// 	void 주문_생성_요청이_정상적으로_처리된다() {
// 		// given
// 		Long cartId = 1L;
// 		OrderRequestDto requestDto = new OrderRequestDto(1000);
// 		OrderResponseDto responseDto = mock(OrderResponseDto.class);
//
// 		when(orderService.createOrder(cartId, requestDto)).thenReturn(responseDto);
//
// 		// when
// 		var response = orderController.createOrder(cartId, requestDto);
//
// 		// then
// 		assertThat(response.getBody()).isEqualTo(responseDto);
// 		verify(orderService).createOrder(cartId, requestDto);
// 	}
//
// 	@Test
// 	void 주문_조회_요청이_정상적으로_처리된다() {
// 		// given
// 		Long orderId = 1L;
// 		OrderResponseDto responseDto = mock(OrderResponseDto.class);
//
// 		when(orderService.findOrder(orderId)).thenReturn(responseDto);
//
// 		// when
// 		var response = orderController.findOrder(orderId);
//
// 		// then
// 		assertThat(response.getBody()).isEqualTo(responseDto);
// 		verify(orderService).findOrder(orderId);
// 	}
//
// 	@Test
// 	void 주문_상태_변경_요청이_정상적으로_처리된다() {
// 		// given
// 		Long orderId = 1L;
// 		OrderStatus newStatus = OrderStatus.IN_DELIVERY;
// 		String expectedMessage = "배송 중입니다.";
//
// 		when(orderService.updateStatus(orderId, newStatus)).thenReturn(expectedMessage);
//
// 		// when
// 		var response = orderController.updateStatus(orderId, newStatus);
//
// 		// then
// 		assertThat(response.getBody()).isEqualTo(Map.of("상태가 업데이트 되었습니다\n", expectedMessage));
// 		verify(orderService).updateStatus(orderId, newStatus);
// 	}
//
// 	@Test
// 	void 주문_취소_요청이_정상적으로_처리된다() {
// 		// given
// 		Long orderId = 1L;
// 		String expectedMessage = "주문이 취소되었습니다.";
//
// 		when(orderService.cancelOrder(orderId)).thenReturn(expectedMessage);
//
// 		// when
// 		var response = orderController.cancelOrder(orderId);
//
// 		// then
// 		assertThat(response.getBody()).isEqualTo(expectedMessage);
// 		verify(orderService).cancelOrder(orderId);
// 	}
// }
