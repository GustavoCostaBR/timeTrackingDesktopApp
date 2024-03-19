package allogica.trackingTimeDesktopApp.model.entity;

import allogica.trackingTimeDesktopApp.utilities.RoleName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;
//
//    @ManyToMany(mappedBy = "roles")
//    private Set<User> users;

	public RoleName getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

    // Constructors, getters, and setters

//    public Role() {
//    }
//
//    public Role(String name) {
//        this.name = name;
//    }
//
//    // Getters and setters
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Set<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(Set<User> users) {
//        this.users = users;
//    }
}
