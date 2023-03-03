package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Orders orders) {
        em.persist(orders);
    }

    public Orders findOne(Long id) {
        return em.find(Orders.class, id);
    }

    public List<Orders> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Orders> cq = cb.createQuery(Orders.class);
        Root<Orders> o = cq.from(Orders.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));

        TypedQuery<Orders> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건

        return query.getResultList();
    }


    public List<Orders> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Orders o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Orders.class
        ).getResultList();
    }

    public List<OrderQuerySimpleDto> findOrderDto() {
        return em.createQuery("select new jpabook.jpashop.repository.OrderQuerySimpleDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        " from Orders o " +
                        " join o.member m" +
                        " join o.delivery d", OrderQuerySimpleDto.class)
                .getResultList();
    }

    public List<Orders> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Orders o" + //TODO : distinct 정리 하기
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Orders.class)
                .getResultList();
    }
}
