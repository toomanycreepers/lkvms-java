package lamart.lkvms.core.entities.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.utilities.exceptions.UserIsNotInOrganizationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users_user", 
       indexes = {
           @Index(name = "idx_user_username", columnList = "username"),
           @Index(name = "idx_user_email", columnList = "email"),
           @Index(name = "idx_user_telegram_chat_id", columnList = "telegram_chat_id", unique = true)
       })
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @Getter
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(length = 255)
    @Getter
    private String name;

    @Column(unique = true)
    @Getter
    private String email;

    @Column(name = "phone_number", length = 255)
    @Getter
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "_selected_organization_id")
    private Organization selectedOrganization;

    @Column(name = "is_active", nullable = false)
    @Getter
    private boolean isActive = true;

    @Column(name = "is_staff", nullable = false)
    @Getter
    private boolean isStaff = false;

    @Column(name = "last_login_ip_address")
    @Getter
    private String lastLoginIpAddress;

    @Column(name = "is_registered", nullable = false)
    @Getter
    private boolean isRegistered = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Getter
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Getter
    private LocalDateTime updatedAt;

    @Column(name = "telegram_chat_id")
    @Getter
    private Long telegramChatId;

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    @Getter
    private Set<Organization> organizations = new HashSet<>();
    
    @Getter
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Getter
    private Set<Role> roles = new HashSet<>();


    public Organization getSelectedOrganization() {
        if (selectedOrganization != null && organizations.contains(selectedOrganization)) {
            return selectedOrganization;
        }
        
        Organization firstOrg = organizations.stream().findFirst().orElse(null);
        this.selectedOrganization = firstOrg;
        return firstOrg;
    }

    public void setSelectedOrganization(Organization organization) 
    throws UserIsNotInOrganizationException {
        if (!organizations.contains(organization)) {
            throw new UserIsNotInOrganizationException();
        }
        this.selectedOrganization = organization;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles==null ? new HashSet<>(): roles;
    }

    @Override
    public String getUsername(){
        return username;
    }

    public String getDisplayName(){
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @Override
    public String toString() {
        return username;
    }
}