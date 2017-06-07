# Interview with Dave Smith, Chief of EMS San Marcos

# INTRODUCTION

"We are gathering background information to understand digital identiy in
emergency responder community. We want to find out how different organizations
are approaching"

- What kinds of digital and in person credenitals FR's are using.
- What kinds of websites and mobile applications FR's are using for work.
- How much friction exists accessing digital resources.

# QUESTIONS FOR END USER

The first few questions are contextual, so we better understand the
information you're sharing:

## Person / Organization:

### Tell us about the purpose of your organization?

   - San Marcos Hays County EMS
   - Website http://smhcems.com
   - 501(c)(3) non-profit public charity providing 911 emergency medical response
   to San Marcos, Kyle, Driftwood, Dripping Springs, Henly, and to portions of
   unincorporated Hays County.
   - SMHC EMS also provides emergency and non-emergency inter-facility transport
   services.

### What is your role?

### What is the size and structure of your organization?
 - 57 full time, 20 PRN (when necessary)
 - 8 Ambulances
 - 11,000 calls per year / 6,500 transports
 - 3rd largest EMS dept in the Austin MTA

 - What do you do spend the largest amount of your time doing?

 Performs generally everything that needs to be done.

### IT Operations
   - Is there a dedicated IT staff?  
     No full-time IT.

   - How much do you rely on IT to deliver the core service?
   Firstnet. Network connectivity. AT&T private contract.
   Connectivity from the field is our biggest challenge.
   Haris LMR. VHS interface to the P25. New county com center.
   IP based alerting. Our lives are nothing but IT.
   3rd party data. We have our own cloud infrastructure.
   0365 is provided by MSFT for free as Non-profit.

   Patient information system - electronic patient care record.
   Which is handed off to the hospital digitally.

   Desktop application that is connect -- ESO.

   - In rough terms, how many usernames and passwords (or other types
    of authentication) do you have? What are some of the systems people need
    to access?

    AD is sync'd with the cloud.
    LMS systems
    Web Page uses LDAP to connect to AD
    Third Party: ESO, Operative IQ, ___ , ___
    Every employee that works in the field.
    Payroll is also managed third party.

   - How concerned are you about security?

   I'm always worried about it. Hoping firstnet has an offering.
   Mobile network has a VPN -- modems in truck are connected
   via VPN.

   CAD - Netmotion (mobile VPN).

   Some systems shared user credentials just to access the computer.

   Need to lock down access host a little better.

   - What could be the range of impacts if your systems were compromised?

   It could impact the business. Health care information act.
   Potential liability for HIPPA act.

   Potentially a new vendor to help with HR / Payroll record retention.

## USER MGT

### Does your organization have a single sign-on system that provides access to multiple websites?

No

### Do all 57/20 EMT/parametics have STRAC cards? How are they used? Are there any mis-uses of the cards? Any places you'd like to use them, but you can't?

Mostly used for in person identification. Physical access control to get into
hospital.

### From an identity management perspective, can you share some thoughts about responding to a large multi-jurisdictional event? How were people identified? Were any not turned away?

Hardest part of the phsycial card is expiration, replacement--takes time.
Tracking what happens to the lost badge? Revocation?
What makes the badge official? The badges have chips? No one is storing
the credentials in a centralized space. Hopeful that firstnet will do this.
Simplifying / re-issuance having less friction would be great.

Quick, reliable,  process and verify identifications really quick. If its not
easy to use, people are not going to use it.

In the field it was so busy, you know your local responders. You know the
team lead. Its their responsibilty to know the team member.

### Can you share your thoughts about using credentials other then passwords (or smartcards) to access websites or mobile applications?

First responders are not going to have access to some kind of device.

A lot of backend systems use two factor authentication.

### What would you think about a mobile identity card for first responders?

### Can you talk a little about the certifications for EMTs and Paramedics:
 - Texas EMS certification
 - National Registry of EMT Tests
 - AHA Training : BLS, ACLS, PALS
 - NAEMT Training: AMLS, PHTLS, EPC, GEMS
 - State of Texas Mobile Intensive Care Unit (MICU) standards

You need to know identity, agency, and what basic certifications they have.
You rely on the leadership of the organization to know their people,
and what skills they have. Large percentage of volunteer--continuous
turnover, various levels of training (people learning on the job),
some people attend training. Differences across states for training.

For major events, there are a limited number of resources that go,
and are deployed by state operations. So they are vetted by state.
Big challenge from people who show up, unrequested. Nightmare
at the command post, trying to figure out what to do with these
people. "self-dispatchers"

### Is conveying training, certifications a challenge? How about in a multi-jurisdictional disaster?

### Do you think people in your organization would object to using to using their person smart phone to apply for a first responder mobile identity card?

### What are the most important organizational applications that your organization sees the need to upgrade within the next five years?

Applications are out there, but they are not tied together. P25
after 9/11... at some point there needs to be some standards
vendors need to meet, so there is a federal credentialing system
to give us single sign-on.

You have to meet this standard for public safety.

employee scheduling... you can do it, but you have to meet the federal
standard for secure authentication.
