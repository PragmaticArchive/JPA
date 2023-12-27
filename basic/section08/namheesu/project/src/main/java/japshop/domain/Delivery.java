package japshop.domain;

import javax.persistence.*;

@Entity
public class Delivery extends BaseEntity{
    @Id @GeneratedValue @Column(name="delivery_id")
    private Long Id;
    private String city;
    private String street;
    private String zipcode;
    private DeliveryStatus status;
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

}
