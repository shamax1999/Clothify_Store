package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "employee")
public class EmployeeEntity {
    @Id
    private String empId;
    private String name;
    private String email;
    private String password;
    private Integer orderCount;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
}
