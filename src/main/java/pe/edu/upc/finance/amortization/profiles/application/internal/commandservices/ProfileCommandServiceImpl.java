package pe.edu.upc.finance.amortization.profiles.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.finance.amortization.profiles.application.internal.outboundservices.acl.ExternalIamService;
import pe.edu.upc.finance.amortization.profiles.domain.model.aggregates.Profile;
import pe.edu.upc.finance.amortization.profiles.domain.model.commands.CreateProfileCommand;
import pe.edu.upc.finance.amortization.profiles.domain.model.valueobjects.EmailAddress;
import pe.edu.upc.finance.amortization.profiles.domain.model.valueobjects.UserId;
import pe.edu.upc.finance.amortization.profiles.domain.services.ProfileCommandService;
import pe.edu.upc.finance.amortization.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;

import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;
    private final ExternalIamService externalIamService;

    public ProfileCommandServiceImpl(
            ProfileRepository profileRepository,
            ExternalIamService externalIamService) {
        this.profileRepository = profileRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        // 1. Validación básica del comando
        if (command == null) {
            throw new IllegalArgumentException("CreateProfileCommand cannot be null");
        }

        // 2. Validar existencia de usuario en IAM
        if (!externalIamService.existsUserByUserId(command.userId())) {
            throw new IllegalArgumentException("User with ID " + command.userId() + " does not exist");
        }

        // 3. Validar existencia de usuario en IAM
        var userId = new UserId(command.userId());
        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " already exists");
        }

        // 4. Validar email único
        var emailAddress = new EmailAddress(command.email());
        profileRepository.findByEmail(emailAddress).ifPresent(profile -> {
            throw new IllegalArgumentException("Profile with email " + command.email() + " already exists");
        });

        // 5. Crear y guardar el perfil
        var profile = new Profile(command);
        var savedProfile = profileRepository.save(profile);

        return Optional.of(savedProfile);
    }
}