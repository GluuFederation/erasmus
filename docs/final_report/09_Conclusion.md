# Conculsion

  *	Challenges
  *	Implications, Takeaways
  *	Recommendations
  *	Call to Action: Phase II Blueprint

In this section we summarize the results and work completed thus far.   We conclude that ERASMUS is beyond a prototype and MVP, meets the requirements of the field, and is ready for field testing.  There are challenges to adoption, these are discussed in this section. A solution to the problem of emergency responder identification seems close at hand--in fact it seems so frustrating close but yet out of reach. Researching both the tools and the rules, leveraging both old and new technologies, and integrating a mobile and backend solution--we believe this pilot will get us a little closer. By providing data, assembling the feedback of many experts, and delivering free open source software based on open standards, the ERASMUS pilot is also likely to result in actual solutions that will be useful to both government and industry.

The fragmented, decentralized nature of the current emergency responder ecosystem in the US, long and thoroughly documented by the policy and research literature can no longer provide the level of security, reliability, scalability necessary for the 64,000 organizations that are part of  the emergency responder community. Put simply: There is currently no universal, interoperable identity infrastructure.

The consequences of a fragmented and outdated system are highly problematic for the first responder themselves as well for the entire emergency community. The lack of a solution puts at risk the safety, security, and accessibility to first responders who are qualified.

In a crisis situation accompanied by chaos and threat, these manual system of t-cards, passport systems, sign in rosters, and smart cards are wholly inadequate for confirming the essential details about an emergency responder, namely their skills, credentials, and authorizations.  What’s needed is a scalable process that relies on the most recent and valid information.  Given the massive number of organizations that are part of the emergency responder community the process is highly adaptable and accessible across multiple jurisdictions.

ERASMUS provides a proof of concept mobile application that demonstrates the potential to access real time information.  ERASMUS is designed to enhance the federated credentials with the cryptographic capabilities of a smartphone. The advantage of ERASMUS is that it relies on state of the art technology in mobile and identity software.  ERASMUS has the capability to protect the privacy of first responders by insuring that federation minimizes the persistence of unencrypted personally identifiable information.


## Challenges

For ERASMUS to succeed we need first responder organizations to align with standards. Three challenges to overcome.  While some of the technology exists to make ERASMUS possible, much work would need to be done to make it a reality. OTTO is still a new standard under development, and this would be one of the first deployments of the technology. While <GFIPM? Others> provides some of the trustmarks needed, its focus is law enforcement--there are be additional trustmarks needed for emergency response considerations. In the mobile application space, while open source authentication apps exist, there is no examples of applications that would perform the kind of signing and trust verification proposed by ERASMUS. There are also important user interface issues that need to be considered to make the application both easy to use, and secure. From a legal perspective, the trust framework for this kind of federation does not exist either. And finally, from an operational perspective, this method of collaboration would provide a new model for collaboration.
Gluu would be prepared to commercialize the software and trust framework that results from this project.  Several opportunities exist to (1) white label the open source for specific organizations--provide a customized and supported release; (2) provide support to organizations that want to use the free open source software; (3) incorporate portions of this technology into the Gluu Server, especially the OTTO endpoints; (4) use the software to launch a managed service for organizations that don’t have the technical capability to deploy and operate the software.

 * Challenge #1.  Governance is the first challenge for the federation is first, creating the governance. The role of Federation is key to ERASMUS’s success because x, y, z.  Federations exist for different organizations and industries. For example, <Mike please categorize>. We advise that the Federation for ERASMUS be modeled after InCommon.  InCommon has several features that are ideal for ERASMUS.

 * Challenge #2.  Size is a second equally important challenge is trying to figure out how to get 65,000 organizations to manage identity in a standard way (by deploying an OpenID Connect Provider). Once that happens, we can create a federation to link them all together, and enable all these great services.

 * Challenge #3.  A third challenge is to get the end user to understand how to generate a trust mark for their organization.
  1.     Location of keys
  2.     Federal laws and policies
  3.      Standards
  4.     Specific requirements for efficiency

## Recommendations for Phase II
Phase II will enable the piloting necessary to show how ERASMUS can transform how first responders and other emergency personnel are authenticated With evidence of its efficacy and scalability, plans could be made to widen the adoption of ERASMUS, and applications could be built on top of it to provide the next generation of emergency responder digital services.  For example, making ERASMUS digital badges becomes easy to present as any other kind of credential. The integration of these standards into ERASMUS platform provides a member jurisdiction’s the security and audit capabilities required for a high level of assurance transaction.

Phase II would seek to prove the feasibility of this platform by continuing to gathering feedback from jurisdictions, piloting a proof-of-concept software stack, and drafting rules for trust management in collaboration with <organizations you propose partnerships>.  Proposed work would also involve several strategies aimed at addressing the core barriers to adoption – governance, technology, and stakeholder buy-in.  

We propose five sets of activities: 1) Governance; 2) Technical development; 3) Pilot & User Testing; 4) Marketing & education; 5) Community outreach and stakeholder engagement

## Governance for ERASMUS.  

The most significant hurdle is not technical, it’s organizational. How to get a diverse network of autonomous organizations to keep the identity information and skills of their people up-to-date and available. And subsequently, how to enable these organizations to share this information in a standard manner that can be communicated to a person in the field who deciding whether to allow entry or assign a task to a person who has responded to an emergency.

Strategy is to form a pilot federation, we build on NIEF or we invite RFP.  A federation offers a coherent and transparent solution. Challenge is creating the governance for ERASMUS.   ERASMUS requires a governance structure that would be representative, sustainable, market responsive.  Governance - would set policies and provide central services to support Trustmark binding to OIDC endpoints in accordance with the spec's trust model.  

Loose Federation The pilot would propose the deployment of a lightweight Federation Operator infrastructure that would support signing of Badges, skills, authorizations, and trustmarks asserted by jurisdictions. The pilot would use OAuth2 profiles to secure connections between the mobile device, the jurisdiction services and the Federation Operator.

## Operationalize Pilot technical Infrastructure
Phase II is about operationalizing the central tech infrastructure, building out ERASMUS on Fedramp secure certified servers.

## Develop offline credential sharing solution
Add text.

## Launch Pilot & User Testing
Add description of how we want to do this.

a)	Usability testing
i)	Organizational onboarding
ii)	User onboarding
iii)	Developer ease of use
b)	Out-of-Scope
i)	Design for non-Internet connected credential validation
ii)	Potential use of OpenID Connect enabled Motorola radios

##  Field Testing
IIdentify an initial group to test - explain selection process or criterion
(1)	Emergency Management Institute (EMI)-FEMA
(2)	National Emergency Management Association (NEMA): https://www.nemaweb.org/index.php/about/what-is-nema
(3)	International Fire Service Accreditation Congress (IFSAC): https://ifsac.org/  https://www.facebook.com/ifsac.org
(4)	American Society for Industrial Security (ASIS)-International: https://www.asisonline.org/Pages/default.aspx
(5)	Emergency Management Association of Texas (EMAT): http://www.emat-tx.org/
(6)	Facebook
(7)	LInkedIn

## Marketing
Challenge to get 65,000+ organizations to use OpenID. Strategy would be to pilot ERASMUS in a focused ecosystem — regional or specific community and pilot

▪	DHS Safety Act would be a good certification to eventually strive for because it almost comes with a guarantee of circulation throughout the industry.  Like a seal of approval: https://www.safetyact.gov/pages/homepages/Home.do.  I believe because the money starts with DHS that there would be some collaboration via that agency.

## Stakeholder engagement
Create consortium of stakeholders to support 1, 2, 3 and to drive ubiquitous adoption / membership. ERASMUS a tool for situational understanding; a contributor to the overall common operating picture (COP).  This is what all operators are trying to achieve in the field:
●	https://www.gridmenow.com/
●	http://www.responsegroupinc.com/  The Response Group or (TRG) is another company out there who has tried to digitize or mobilize the Incident Action Plan (IAP).  I’ve never used them myself but I generally don’t hear many good things about them.
industry.
