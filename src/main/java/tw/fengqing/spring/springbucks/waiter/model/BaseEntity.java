package tw.fengqing.spring.springbucks.waiter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
// 說明：
// 以往在使用 Hibernate 時，實體物件會自動帶有 hibernateLazyInitializer 等屬性，
// 若未加以忽略，Jackson 在序列化時會報錯，因此常見寫法是加上 @JsonIgnoreProperties({"hibernateLazyInitializer"})
// 但自從專案引入 jackson-datatype-hibernate6 這個模組後，Jackson 能自動處理 Hibernate 的延遲加載屬性，
// 不會再將 hibernateLazyInitializer 等內部屬性序列化到 JSON，也不會拋出相關例外，
// 所以這個 Ignore 註解就不需要再加了，程式碼更簡潔，維護也更方便。
//@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(updatable = false)
    @CreationTimestamp
    private Date createTime;
    @UpdateTimestamp
    private Date updateTime;
}
