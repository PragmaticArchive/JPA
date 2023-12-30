package japshop.domain;

import javax.persistence.*;

@Entity
public class Delivery extends BaseEntity{
    @Id @GeneratedValue @Column(name="delivery_id")
    private Long Id;
    @Embedded
    private Address address;
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

}
