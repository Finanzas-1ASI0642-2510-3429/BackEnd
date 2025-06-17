package pe.edu.upc.finance.amortization.profiles.domain.model.aggregates;

import jakarta.persistence.*;
import pe.edu.upc.finance.amortization.profiles.domain.model.commands.CreateProfileCommand;
import pe.edu.upc.finance.amortization.profiles.domain.model.valueobjects.EmailAddress;
import pe.edu.upc.finance.amortization.profiles.domain.model.valueobjects.PersonName;
import pe.edu.upc.finance.amortization.profiles.domain.model.valueobjects.StreetAddress;
import pe.edu.upc.finance.amortization.profiles.domain.model.valueobjects.UserId;
import pe.edu.upc.finance.amortization.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

/**
 * Represents a Profile entity that aggregates personal information, including name, email, address, and user ID.
 * This class is auditable and extends from {@code AuditableAbstractAggregateRoot}.
 * It provides methods to manipulate and retrieve personal information.
 *
 * The Profile entity has the following attributes:
 * - A person's name, represented by {@code PersonName}.
 * - An email address, represented by {@code EmailAddress}.
 * - A street address, represented by {@code StreetAddress}.
 * - A unique user ID, represented by {@code UserId}.
 *
 * The Profile class supports operations for creating and updating the entity's attributes and provides access
 * to formatted representations of personal details.
 */
@Entity
public class Profile extends AuditableAbstractAggregateRoot<Profile> {

    @Embedded
    private PersonName name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "email_address"))})
    EmailAddress email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "number", column = @Column(name = "address_number")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country"))})
    private StreetAddress address;

    @Embedded
    private UserId userId;

    public Profile(String firstName, String lastName, String email, String street, String number, String city, String postalCode, String country, Long userId) {
        this.name = new PersonName(firstName, lastName);
        this.email = new EmailAddress(email);
        this.address = new StreetAddress(street, number, city, postalCode, country);
        this.userId = new UserId(userId);
    }

    public Profile(CreateProfileCommand command) {
        this.name = new PersonName(command.firstName(), command.lastName());
        this.email = new EmailAddress(command.email());
        this.address = new StreetAddress(command.street(), command.number(), command.city(), command.postalCode(), command.country());
        this.userId = new UserId(command.userId());
    }

    public Profile() {
    }

    public void updateName(String firstName, String lastName) {
        this.name = new PersonName(firstName, lastName);
    }

    public void updateEmail(String email) {
        this.email = new EmailAddress(email);
    }

    public void updateAddress(String street, String number, String city, String postalCode, String country) {
        this.address = new StreetAddress(street, number, city, postalCode, country);
    }

    public String getFullName() {
        return name.getFullName();
    }

    public String getEmailAddress() {
        return email.address();
    }

    public String getStreetAddress() {
        return address.getStreetAddress();
    }
}