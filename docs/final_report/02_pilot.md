# Product Overview

ERASMUS is a system that demonstrates the potential of the federated identity infrastructure. It comprises of 1) framework; 2) standards; and 3) proof of concept mobile application.  

ERASMUS decentralizes the registration of participants. For example, if you are a sheriff or fire department, you want to register organization into the federation, you use FIDES to register self and register organization.

ERASMUS digitally identifies people and retrieve current information.  

  * Local organizations will review and approve the issuance of badges.
  *	Local revocation of credentials will be effective immediately.
  *	ERASMUS will hold information about organizations, people, their skills, and location. ERASMUS will have the ability to push notifications to registered devices.
  *	This infrastructure will enable the construction of a next generation of identity aware digital services.

  ERASMUS would be supported by a federation that is 1) decentralized; 2) lower assurance; 3) streamlined and not weighted down by overly bureaucratic procedures that are hard to update and work against the community’s need to come together on a common platform; 4) transparent; 5) flexible

The ERASMUS Federation Operator is the key role. He or she approves participant applications and identity provider, relying party publication requests.  Other roles in the ERASMUS system include:

  * Organizational Administrator:  
    1. Register organization;
  	2. Register OP;
  	3. Manage badges (create, approve)
  *	Credential Asserter:
  	1. Configure app;
  	2. Download badges;
  	3. Asset badge
  *	Credential Validator- Validate badge
  *	Developer - Develop websites / API’s that rely on federation technical services

ERASMUS offers a comprehensive solution to the most significant challenges by offering coherence, transparency, updated technology, increased accessibility, scalabilty.

## How ERASMUS Works

  * Step 1: If the person has a smart card, it can be used to login to a website, and enroll a mobile device. This is normally accomplished by displaying a QR code (post authentication) that the person scans, which kicks off a process of key generation and registration on the jurisdiction’s OAuth2 server. In this way, the registration of the mobile credential can be tied back to the smart card as a derived credential.
 *	Step 2: If no smart card is present, the mobile enrollment will have to be started by a different process. Ideally, post identity proofing, the person would be issued an initial password credential, and would immediately register a mobile device. This way, the password is only used for the initial enrollment, and subsequent authentication happens via the mobile device, or another strong identity credential (for example, a hardware authentication token).
 *	Step 3: Post-authentication, the person’s mobile device requests one or more Badges from the federation operator by calling the Identity Endpoint API. Note, the mobile device can be configured to automatically download an updated Badge periodically or when the badge expires.
 *	Step 4: In order to call the Identity Endpoint API, the Federation Operator relies on the jurisdiction to ensure that both the client (the app running on the phone) and the method of authentication for the person is sufficient. While the schema for this would be standard (for example, perhaps “nist-level-3”), how the jurisdiction implements the authentication could vary. For example, one jurisdiction may use an SaaS mobile / biometric service, while another uses PIV cards. This will enable jurisdictions to select the authentication technology that meets the deployment requirements that is appropriate for their scale.
 *	Step 5: The Identity API will return a redirect URI to the mobile client, which will in turn redirect the person to authorize the release of attributes to the Federation Operator. Note the source of the attributes is the jurisdiction that has issued the credential. The jurisdiction would also supply the trustmarks that define the identity management standards to which it adheres. The Federation Operator is merely signing the a JSON containing the information asserted by the jurisdiction.
 *	Step 6: With successful creation of the Badge, it is returned to the client. It can be viewed by the Person in their Badge “backpack”, and it is ready to be presented for verification. Because it is a signed document of the Federation Operator, integrity is assured--the document cannot be modified by the person or an attacker.
 *	Step 7: In the final step, the Badge is sent to the person who needs to validate the credential. The public key of the federation operator is needed, which can be downloaded ahead of time, to decrypt the badge metadata. The app may download some older keys to validate older Badges. 

## Mobile & Identity Technology
 ERASMUS is designed to enhance the federated credentials with the cryptographic capabilities of a smartphone.  The ERASMUS prototype leverage badges – digitally signed documents embedded in an image, as described throughout the  report and detailed in Section II.  It enables cryptographic verification of a first responder’s credentials.  The mobile interface will allow first responders  and other emergency personnel from all jurisdictions to align with open standards that can be adapted and used in a variety of settings and conditions, in real time.

Current system is inadequate for current needs, emergency responder community needs faster, inter-agency, skills, security. 
The creation of ERASMUS as a federation and a digital identiy system will solve multiple problems for the emergency responder community. ERASMUS could potentially provide a comprehensive infrastructure that would further enable the construction of a next generation of identity aware digital services.
