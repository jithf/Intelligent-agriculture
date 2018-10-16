package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Auther: zj
 * @Date: 2018/6/7 20:30
 * @Description:
 */
public interface OrderInfoRepository extends JpaRepository<OrderInfo,Long> {
    OrderInfo findOneByOrderId(String orderId);
    List<OrderInfo> findAllByAlipayNo(String alipayNo);
}
