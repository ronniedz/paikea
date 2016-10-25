package cab.bean.srvcs.tube4kids.core;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name = "age_group")
@NamedQueries({ @NamedQuery(name = "cab.bean.srvcs.tube4kids.core.AgeGroup.findAll", query = "SELECT p FROM AgeGroup p") })
@Data
public class AgeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start", nullable = false)
    private Boolean start;

    @Column(name = "end", nullable = false)
    private Boolean end;

    @Column(name = "label", nullable = false)
    private String label;
}
