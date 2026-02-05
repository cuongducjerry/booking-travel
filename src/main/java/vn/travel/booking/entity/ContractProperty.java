package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract_property")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private HostContract contract;

    @ManyToOne
    private Property property;
}
