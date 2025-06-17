package pe.edu.upc.finance.amortization.profiles.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import pe.edu.upc.finance.amortization.iam.interfaces.acl.IamContextFacade;

@Service
public class ExternalIamService {
    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public boolean existsUserByUserId(Long userId) {
        try {
            return iamContextFacade.existsUserByUserId(userId);
        } catch (Exception e) {
            // Log y manejo adecuado para tu contexto
            return false;
        }
    }
}
