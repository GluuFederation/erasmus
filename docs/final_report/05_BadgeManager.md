# Overview

Badge Manager is one of the important and key components of the ERASMUS as it contains the APIs which are exposing to mobile app and FIDES.

Badge requests, Badge classes and Badge assertions (In short, `Badges`) are defined in Badge Manager.

Badge Manager is the heart of the mobile app as mobile app only connects with Badge Manager and it works as a bridge between the mobile app and FIDES as well.

[Badge Manager Swagger URL](https://erasmusdev.gluu.org/badge-mgr/swagger-ui.html)

Badge Manager performs following operations by exposing APIs.

    (1) List of participants 
    (2) List of template badges
    (3) Making badge request
    (4) List of badge request to FIDES
    (5) Approves badge request
    (6) List of badge requests to Mobile app
    (7) Delete badge request
    (8) Set privacy for approved badge request
    (9) Request a badge
    (10) Verify badge and provide actual badge information
    (11) Temporary and permanent badge link 
    (12) Grant and revoke badge access to FIDES    
      
### Technology
Technologies used to develop Badge Manager are:

    1. JAVA Spring boot
    2. LDAP
    3. Redis
    4. OXD Server
    5. E3DB
    6. Swagger
            
Let's understand each operation briefly
      
### (1) List of Participants
`Participants are defined and managed in FIDES.`

Badge Manager retrieve participants from FIDES based on city and state provided from user from mobile app and returns participants list to user.

API Endpoint: `/participants`

Method: `GET`

Params: `/state/city`

Request: `https://erasmusdev.gluu.org/badge-mgr/participants/Colorado/Arapahoe`

Response:
 ```json
 {
   "participants": [
     {
       "id": "58fdb832da495fae4d6a3987",
       "context": "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#participant",
       "opHost": "https://ce-dev2.gluu.org",
       "name": "LOCAL ORG SERVER",
       "phoneNo": "22221333366",
       "address": "C123 LOCAL",
       "zipcode": "21415",
       "city": "Arapahoe",
       "state": "Colorado",
       "type": "service"
     }
   ],
   "error": false
 }
 ```

### (2) List of template badges
`Template badges are defined and managed in FIDES.` 

Badge Manager retrieve template badges from FIDES for participant(retrieved from user from mobile app) and returns template badges list to user.
 
API Endpoint: `/badges/templates`

Method: `POST`

Params: 
 ```json
 {
   "opHost": "https://gluu.local.org",
   "type": "all"
 }
 ```

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/templates`
      
Response:
 ```json
  {
    "badges": [
      {
        "id": "https://erasmus.gluu.org:8000/templateBadge/58fdb776da495fae4d6a3985",
        "name": "Entry-Level2 Firefighter",
        "description": "Instruction lasts several weeks and teaches building codes, emergency medical procedures and prevention techniques. Plus, programs train students to fight fires with standard equipment, such as fire extinguishers, ladders, axes and chainsaws",
        "image": "https://erasmus.gluu.org:8000/images/badges/7baf23fa-2c59-41fb-be97-b0e42620498f.png",
        "narrative": "After academy training, firefighters need to complete an apprenticeship of up to four years. Some fire departments send students for additional education with the National Fire Academy, where they learn disaster preparedness, public education and how to handle hazardous materials",
        "type": "BadgeClasses",
        "issuer": {
          "id": "https://erasmus.gluu.org:8000/participant/5923f1de8bc19a19d7e939c3",
          "name": "LOCAL ORG3",
          "type": "Service",
          "url": "https://erasmusdev.gluu.org",
          "verification": {
            "allowedOrigins": "https://erasmusdev.gluu.org",
            "type": "hosted"
          }
        }
      },
      {
        "id": "https://erasmus.gluu.org:8000/templateBadge/58fdb7afda495fae4d6a3986",
        "name": "Entry-Level3 Firefighter",
        "description": "Instruction lasts several weeks and teaches building codes, emergency medical procedures and prevention techniques. Plus, programs train students to fight fires with standard equipment, such as fire extinguishers, ladders, axes and chainsaws",
        "image": "https://erasmus.gluu.org:8000/images/badges/f89520fe-9ee1-4574-9e33-2b84ac5e786c.png",
        "narrative": "After academy training, firefighters need to complete an apprenticeship of up to four years. Some fire departments send students for additional education with the National Fire Academy, where they learn disaster preparedness, public education and how to handle hazardous materials",
        "type": "BadgeClasses",
        "issuer": {
          "id": "https://erasmus.gluu.org:8000/participant/5923f1de8bc19a19d7e939c3",
          "name": "LOCAL ORG3",
          "type": "Service",
          "url": "https://erasmusdev.gluu.org",
          "verification": {
            "allowedOrigins": "https://erasmusdev.gluu.org",
            "type": "hosted"
          }
        }
      },
      {
        "id": "https://erasmus.gluu.org:8000/templateBadge/58e49bec0cd5268169f7576a",
        "name": "Entry-Level Firefighter",
        "description": "Instruction lasts several weeks and teaches building codes, emergency medical procedures and prevention techniques. Plus, programs train students to fight fires with standard equipment, such as fire extinguishers, ladders, axes and chainsaws",
        "image": "https://erasmus.gluu.org:8000/images/badges/5cebf6a5-934e-41b9-b463-9d85e6e03756.png",
        "narrative": "After academy training, firefighters need to complete an apprenticeship of up to four years. Some fire departments send students for additional education with the National Fire Academy, where they learn disaster preparedness, public education and how to handle hazardous materials",
        "type": "BadgeClasses",
        "issuer": {
          "id": "https://erasmus.gluu.org:8000/participant/5923f1de8bc19a19d7e939c3",
          "name": "LOCAL ORG3",
          "type": "Service",
          "url": "https://erasmusdev.gluu.org",
          "verification": {
            "allowedOrigins": "https://erasmusdev.gluu.org",
            "type": "hosted"
          }
        }
      },
      {
        "id": "https://erasmus.gluu.org:8000/templateBadge/58e498230cd5268169f75761",
        "name": "Emergency Medical Technician-Basic",
        "description": "EMT-Basic training requires about 100 hours of instruction, including practice in a hospital or ambulance",
        "image": "https://erasmus.gluu.org:8000/images/badges/7847e312-ccf3-4a3f-b516-433d7da7db63.png",
        "narrative": "EMT-Basic students must pass an exam testing the ability to assess patient condition, handle trauma or cardiac emergencies and clear blocked airways. They also learn to immobilize injured patients and give oxygen.",
        "type": "BadgeClasses",
        "issuer": {
          "id": "https://erasmus.gluu.org:8000/participant/5923f1de8bc19a19d7e939c3",
          "name": "LOCAL ORG3",
          "type": "Service",
          "url": "https://erasmusdev.gluu.org",
          "verification": {
            "allowedOrigins": "https://erasmusdev.gluu.org",
            "type": "hosted"
          }
        }
      }
    ],
    "error": false
  }
  ```  
   
### (3) Making badge request
This API allows user to request particular badge by selecting template badge from the list in mobile app.

Badge requests are persisted in LDAP.

API Endpoint: `/badges/request`

Method: `POST`

Params: 
```json
{
  "opHost": "https://gluu.local.org",
  "participant": "5923f1de8bc19a19d7e939c3",
  "templateBadgeId": "58fdb776da495fae4d6a3985",
  "templateBadgeTitle": "Entry-Level2 Firefighter"
}
```

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/request`

Response:
```json
{
  "badgeRequest": {
    "inum": "@!4301.2A50.9A09.7688!1002!F840.E189",
    "participant": "5923f1de8bc19a19d7e939c3",
    "templateBadgeId": "58fdb776da495fae4d6a3985",
    "templateBadgeTitle": "Entry-Level2 Firefighter",
    "status": "Pending",
    "requesterEmail": "megtest3@gluu.org",
    "privacy": null
  },
  "error": false
}
```

### (4) List of badge requests (FIDES) 
This API is exposed to FIDES which returns badge requests (approved or pending) for particular participant.

API Endpoint: `/badges/request/list/{participant}/{status}`

Method: `GET`

Params: `/participant/status`

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/request/list/5923f1de8bc19a19d7e939c3/pending`

Response: 
```json
{
  "badgeRequests": [
    {
      "inum": "@!4301.2A50.9A09.7688!1002!94AE.5312",
      "participant": "5923f1de8bc19a19d7e939c3",
      "templateBadgeId": "58fdb7afda495fae4d6a3986",
      "templateBadgeTitle": "Entry-Level3 Firefighter",
      "status": "Pending",
      "requesterEmail": "megtest3@gluu.org",
      "privacy": "Public"
    },
    {
      "inum": "@!4301.2A50.9A09.7688!1002!F840.E189",
      "participant": "5923f1de8bc19a19d7e939c3",
      "templateBadgeId": "58fdb776da495fae4d6a3985",
      "templateBadgeTitle": "Entry-Level2 Firefighter",
      "status": "Pending",
      "requesterEmail": "megtest3@gluu.org",
      "privacy": "Public"
    },
    {
      "inum": "@!4301.2A50.9A09.7688!1002!7F88.C7DD",
      "participant": "5923f1de8bc19a19d7e939c3",
      "templateBadgeId": "58e498230cd5268169f75761",
      "templateBadgeTitle": "Emergency Medical Technician-Basic",
      "status": "Pending",
      "requesterEmail": "megtest3@gluu.org",
      "privacy": "Public"
    }
  ],
  "error": false
}
```

### (5) Approves badge request (FIDES)
 
`Only FIDES can approve badge request.`
 
An API exposed to FIDES to approve badge request. 

Badge request get approved (by changing badge request status to `Approved` in LDAP) and badge entry get persisted in LDAP as soon as badge request get approved. 
 
API Endpoint: `/badges/request/approve`

Method: `POST`
 
Params: 
 ```json
{
  "inum": "@!4301.2A50.9A09.7688!1002!7F88.C7DD",
  "privacy": "Public",
  "validity": 3
}
```

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/request/approve`

Response:
```json
{
  "message": "Badge request approved successfully",
  "error": false
}
```

### (6) List of badge requests (Mobile app)
This API returns approved and pending badge requests which get displayed in mobile app.

API Endpoint: `/badges/request/list`

Method: `POST`

Params:
```json
{
  "opHost": "https://gluu.local.org",
  "status": "all"
}
```    

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/request/list`

Response:
```json
{
  "error": false,
  "badgeRequests": {
    "pendingBadgeRequests": [
      {
        "inum": "@!4301.2A50.9A09.7688!1002!C3C1.1253",
        "participant": "5923f1de8bc19a19d7e939c3",
        "templateBadgeId": "58e498230cd5268169f75761",
        "templateBadgeTitle": "Emergency Medical Technician-Basic",
        "status": "Pending",
        "requesterEmail": "arvind@gluu.org",
        "privacy": ""
      },
      {
        "inum": "@!4301.2A50.9A09.7688!1002!1ECB.A00C",
        "participant": "5923f1de8bc19a19d7e939c3",
        "templateBadgeId": "58fdb7afda495fae4d6a3986",
        "templateBadgeTitle": "Entry-Level3 Firefighter",
        "status": "Pending",
        "requesterEmail": "arvind@gluu.org",
        "privacy": ""
      }
    ],
    "approvedBadgeRequests": [
      {
        "inum": "@!4301.2A50.9A09.7688!1002!0B93.AA10",
        "participant": "5923f1de8bc19a19d7e939c3",
        "templateBadgeId": "58e49bec0cd5268169f7576a",
        "templateBadgeTitle": "Entry-Level Firefighter",
        "status": "Approved",
        "requesterEmail": "arvind@gluu.org",
        "privacy": "Private"
      },
      {
        "inum": "@!4301.2A50.9A09.7688!1002!187B.AF13",
        "participant": "5923f1de8bc19a19d7e939c3",
        "templateBadgeId": "58fdb776da495fae4d6a3985",
        "templateBadgeTitle": "Entry-Level2 Firefighter",
        "status": "Approved",
        "requesterEmail": "arvind@gluu.org",
        "privacy": "Public"
      }
    ]
  }
}
```

### (7) Delete badge request
User can use to delete badge request from mobile app using this API.

It will remove badge request and badge entry from LDAP permanently.
 
API Endpoint: `/badges/request/delete` 

Method: `DELETE`

Params:
```json
{
  "badgeRequestInum": "@!4301.2A50.9A09.7688!1002!187B.AF13",
  "opHost": "https://gluu.local.org"
}
```

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/request/delete`

Response:
```json
{
  "error": false,
  "message": "Badge Request deleted successfully"
}
```

### (8) Set privacy for approved badge request    
This API provides facility to the user to set badge privacy to `public` or `private` from mobile app.

By default badge privacy will be public which user can change through this API for approved badge requests.

User can set badge privacy only to approved badge requests as badge entry get persisted in LDAP only after badge request get approved.

API Endpoint: `/badges/setPrivacy`

Method: `POST`    

Params: 
```json
{
  "badgeRequestInum": "@!4301.2A50.9A09.7688!1002!187B.AF13",
  "opHost": "https://gluu.local.org",
  "privacy": "Private"
}    
```

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/setPrivacy`

Response:
```json
{
  "error": false,
  "message": "Badge privacy set to Private successfully"
}
```

### (9) Request a badge
User can request a badge for approved badge request by using the API exposed by Badge Manager to mobile app.

Badge manager generates badge QR code and return basic badge info along with badge QR code image url and user info.

This API will be used by `Asserter` to present badge to the `Validator` in mobile app for badge verification.
 
API Endpoint: `/badges/details` 

Method: `POST`

Params: 
```json
{
  "badgeRequestInum": "@!4301.2A50.9A09.7688!1002!187B.AF13",
  "opHost": "https://gluu.local.org"
}
```

Request: `https://erasmusdev.gluu.org/badge-mgr/badges/details`

Response:
```json
{
  "qrCode": "https://erasmusdev.gluu.org/badge-mgr/images/1496058950730.png",
  "expiresAt": "Mon May 29 11:57:25 UTC 2017",
  "badgeTitle": "Entry-Level2 Firefighter",
  "badgePublicURL": "https://erasmusdev.gluu.org/badge-mgr/badges/verify/33fc298b-dd11-4816-9f4f-afcd11bfb218",
  "recipient": {
    "type": "text",
    "identity": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0OGQ1NDFlMy1iOTEwLTQwNTUtYTRlYy1jMzc5ODA4OWU5MjIiLCJzdWIiOiJ0ZXN0IiwiaXNzIjoiaHR0cDovL3d3dy5nbHV1Lm9yZyIsInVzZXJpbmZvIjoie1wiZW1haWxcIjpcInRlc3RfdXNlckB0ZXN0Lm9yZ1wiLFwiZW1haWxfdmVyaWZpZWRcIjpcInRydWVcIixcInN1YlwiOlwiSzZzQmpRa1pRbDNSUC1YSUxhMWdMYTJrMjExenY0QmdvVkpDdHZmUlpqQVwiLFwiem9uZWluZm9cIjpcIkFtZXJpY2EvQ2hpY2Fnb1wiLFwibmlja25hbWVcIjpcInVzZXJcIixcIndlYnNpdGVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmdcIixcIm1pZGRsZV9uYW1lXCI6XCJVc2VyXCIsXCJsb2NhbGVcIjpcImVuLVVTXCIsXCJwcmVmZXJyZWRfdXNlcm5hbWVcIjpcInVzZXJcIixcImdpdmVuX25hbWVcIjpcIlRlc3RcIixcInBpY3R1cmVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmcvd3AtY29udGVudC91cGxvYWRzLzIwMTIvMDQvbWlrZTMucG5nXCIsXCJ1cGRhdGVkX2F0XCI6XCIyMDE3MDIyNDEyNTkxNS41MzhaXCIsXCJuYW1lXCI6XCJveEF1dGggVGVzdCBVc2VyXCIsXCJiaXJ0aGRhdGVcIjpcIjE5ODMtMS02XCIsXCJmYW1pbHlfbmFtZVwiOlwiVXNlclwiLFwiZ2VuZGVyXCI6XCJNYWxlXCIsXCJwcm9maWxlXCI6XCJodHRwOi8vd3d3Lm15d2Vic2l0ZS5jb20vcHJvZmlsZVwifSIsImlhdCI6MTQ5NjA1NDc5N30.vkKS4k37a4DmehgLIshl1D0aFt0JRl2Wr-OWzzJNKFDj_uxekoDlk5_VSKU9whUGikyhKE4TkPz0jAOaxN58KQ"
  }
}
```

### (10) Verify badge and provide actual badge information
Badge manager exposes an APIs to mobile app to verify badge and returns actual badge information after successful verification.

This process will get completed in 3 steps:

1. When `Validator` scans badge qr code in order to verify badge, notification will be sent to `Asserter`
 which prompt with message that some one wants to see his/her badge along with `Allow` and `Deny` options.
 
    At the time of badge QR code image creation, Id of the badge stored as a data in QR code.
    
    This Id will be used in following API to send notification to the `Asserter`.
    
    API Endpoint: `/notification/send` 
    
    Method: `POST`
    
    Params: 
    ```json
    {
      "badge": "52e80fad-beb6-47e7-bd07-1f3a89a16978",
      "opHost": "https://gluu.local.org",
      "participant": "LOCAL ORG"
    }
    ```
    
    Request: `https://erasmusdev.gluu.org/badge-mgr/notification/send`
    
    Response:
    ```json
    {
      "error": false,
      "message": "Notification sent successfully"
    }
    ```    
 
2. `Asserter` sets badge permission
    
    `Asserter` can set permission of badge access to `Validator` using following API which will notify `Validator` the same.
    
     API Endpoint: `/badges/setPermission` 
        
     Method: `POST`
        
     Params: 
     ```json
     {
       "access": "true",
       "badge": "52e80fad-beb6-47e7-bd07-1f3a89a16978",
       "opHost": "https://gluu.local.org"
     }
     ```
     
     Request: `https://erasmusdev.gluu.org/badge-mgr/badges/setPermission`
     
     Response:
     ```json
     {
       "error": false,
       "message": "Permission to see this badge granted successfully"
     }
     ```
     If `Asserter` chooses `Allow`, then temporary link of the badge which is an endpoint for retrieving actual badge information get generated and sent to `Validator` along with notification using which validator
     can validate the badge and see actual information.
     
     If `Asserter` chooses `Deny`, then `Validator` will not be able to see the badge.
    
3. `Asserter` will be notified after successful badge verification.

    For badge privacy, two GUIDs `Id` and `Key` are used to secure badge information and temporary link is generated in a way that it's become hard to guess.
 
    `for public badge only Id will be used whereas for private badge both Id and Key will be used in verification as well link generation.`
 
    Hence, There are two different APIs which will verify public and private badges.
 
    Public badge will be verified by matching only Id(random generated string) with LDAP badge record and it will return actual badge (Badge assertion) data in a format define in Open Badge specification if matched.

    Private badge will get verified by matching both Id and Key.

    `Note: Temporary link will get expired in 90 seconds.`
     
    API Endpoint: `/tmp/{id}`
      
    Method: `GET`
         
    Params: `id`
     
    - For Public badge
    
        Request: `https://erasmusdev.gluu.org/badge-mgr/tmp/764473bf`
            
        Response:    
        ```json
        {  
           "context":"https://w3id.org/openbadges/v2",
           "id":"https://erasmusdev.gluu.org/badge-mgr/badges/verify/33fc298b-dd11-4816-9f4f-afcd11bfb218",
           "type":"Assertion",
           "issuedOn":"Mon May 29 10:46:37 UTC 2017",
           "expires":"",
           "recipient":{  
              "type":"text",
              "identity":"eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0OGQ1NDFlMy1iOTEwLTQwNTUtYTRlYy1jMzc5ODA4OWU5MjIiLCJzdWIiOiJ0ZXN0IiwiaXNzIjoiaHR0cDovL3d3dy5nbHV1Lm9yZyIsInVzZXJpbmZvIjoie1wiZW1haWxcIjpcInRlc3RfdXNlckB0ZXN0Lm9yZ1wiLFwiZW1haWxfdmVyaWZpZWRcIjpcInRydWVcIixcInN1YlwiOlwiSzZzQmpRa1pRbDNSUC1YSUxhMWdMYTJrMjExenY0QmdvVkpDdHZmUlpqQVwiLFwiem9uZWluZm9cIjpcIkFtZXJpY2EvQ2hpY2Fnb1wiLFwibmlja25hbWVcIjpcInVzZXJcIixcIndlYnNpdGVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmdcIixcIm1pZGRsZV9uYW1lXCI6XCJVc2VyXCIsXCJsb2NhbGVcIjpcImVuLVVTXCIsXCJwcmVmZXJyZWRfdXNlcm5hbWVcIjpcInVzZXJcIixcImdpdmVuX25hbWVcIjpcIlRlc3RcIixcInBpY3R1cmVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmcvd3AtY29udGVudC91cGxvYWRzLzIwMTIvMDQvbWlrZTMucG5nXCIsXCJ1cGRhdGVkX2F0XCI6XCIyMDE3MDIyNDEyNTkxNS41MzhaXCIsXCJuYW1lXCI6XCJveEF1dGggVGVzdCBVc2VyXCIsXCJiaXJ0aGRhdGVcIjpcIjE5ODMtMS02XCIsXCJmYW1pbHlfbmFtZVwiOlwiVXNlclwiLFwiZ2VuZGVyXCI6XCJNYWxlXCIsXCJwcm9maWxlXCI6XCJodHRwOi8vd3d3Lm15d2Vic2l0ZS5jb20vcHJvZmlsZVwifSIsImlhdCI6MTQ5NjA1NDc5N30.vkKS4k37a4DmehgLIshl1D0aFt0JRl2Wr-OWzzJNKFDj_uxekoDlk5_VSKU9whUGikyhKE4TkPz0jAOaxN58KQ"
           },
           "verification":{  
              "type":"hosted"
           },
           "badge":{  
              "type":"BadgeClass",
              "id":"https://erasmusdev.gluu.org/badge-mgr/badgeClass/82377ffe-569d-497c-abcc-3a0098757845?key=XSGQ0Q62PUBA",
              "name":"Entry-Level2 Firefighter",
              "description":"Instruction lasts several weeks and teaches building codes, emergency medical procedures and prevention techniques. Plus, programs train students to fight fires with standard equipment, such as fire extinguishers, ladders, axes and chainsaws",
              "image":"https://erasmus.gluu.org:8000/images/badges/7baf23fa-2c59-41fb-be97-b0e42620498f.png",
              "criteria":{  
                 "narrative":"After academy training, firefighters need to complete an apprenticeship of up to four years. Some fire departments send students for additional education with the National Fire Academy, where they learn disaster preparedness, public education and how to handle hazardous materials"
              },
              "issuer":{  
                 "id":"https://erasmusdev.gluu.org",
                 "type":"Profile",
                 "name":"Erasmus",
                 "url":"https://erasmusdev.gluu.org",
                 "email":"erasmussupport@gluu.org",
                 "verification":{  
                    "allowedOrigins":"https://erasmusdev.gluu.org",
                    "type":"hosted"
                 }
              }
           }
        }
        ```  
    
    - For Private badge
    
        Request: `https://erasmusdev.gluu.org/badge-mgr/tmp/48e04dce`
         
        Response:    
        ```json
        {  
           "context":"https://w3id.org/openbadges/v2",
           "id":null,
           "type":"Assertion",
           "issuedOn":"Wed May 24 14:15:39 UTC 2017",
           "expires":"Mon May 29 12:39:59 UTC 2017",
           "recipient":{  
              "type":"text",
              "identity":"eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJhMDdhMDcxYi1hY2VmLTRmZmEtYjI3Zi01Y2M1M2YyN2Q1NWMiLCJzdWIiOiJ0ZXN0IiwiaXNzIjoiaHR0cDovL3d3dy5nbHV1Lm9yZyIsInVzZXJpbmZvIjoie1wiZW1haWxcIjpcInRlc3RfdXNlckB0ZXN0Lm9yZ1wiLFwiZW1haWxfdmVyaWZpZWRcIjpcInRydWVcIixcInN1YlwiOlwiSzZzQmpRa1pRbDNSUC1YSUxhMWdMYTJrMjExenY0QmdvVkpDdHZmUlpqQVwiLFwiem9uZWluZm9cIjpcIkFtZXJpY2EvQ2hpY2Fnb1wiLFwibmlja25hbWVcIjpcInVzZXJcIixcIndlYnNpdGVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmdcIixcIm1pZGRsZV9uYW1lXCI6XCJVc2VyXCIsXCJsb2NhbGVcIjpcImVuLVVTXCIsXCJwcmVmZXJyZWRfdXNlcm5hbWVcIjpcInVzZXJcIixcImdpdmVuX25hbWVcIjpcIlRlc3RcIixcInBpY3R1cmVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmcvd3AtY29udGVudC91cGxvYWRzLzIwMTIvMDQvbWlrZTMucG5nXCIsXCJ1cGRhdGVkX2F0XCI6XCIyMDE3MDIyNDEyNTkxNS41MzhaXCIsXCJuYW1lXCI6XCJveEF1dGggVGVzdCBVc2VyXCIsXCJiaXJ0aGRhdGVcIjpcIjE5ODMtMS02XCIsXCJmYW1pbHlfbmFtZVwiOlwiVXNlclwiLFwiZ2VuZGVyXCI6XCJNYWxlXCIsXCJwcm9maWxlXCI6XCJodHRwOi8vd3d3Lm15d2Vic2l0ZS5jb20vcHJvZmlsZVwifSIsImlhdCI6MTQ5NTYzNTMzOX0.c40m8nWnlObUtYdUHtcCOc3QLy7EWISbwoEVFAnb9BwC__tZMeAuxSsNfL1jPucW4WIN4SdeWnmX6mesiJaWLQ"
           },
           "verification":{  
              "type":"hosted"
           },
           "badge":{  
              "type":"BadgeClass",
              "id":"https://erasmusdev.gluu.org/badge-mgr/badgeClass/f8994deb-9f00-45b5-95e1-d221546f6a93?key=TCHMPHRV2U8P",
              "name":"Entry-Level Firefighter",
              "description":"Instruction lasts several weeks and teaches building codes, emergency medical procedures and prevention techniques. Plus, programs train students to fight fires with standard equipment, such as fire extinguishers, ladders, axes and chainsaws",
              "image":"https://erasmus.gluu.org:8000/images/badges/5cebf6a5-934e-41b9-b463-9d85e6e03756.png",
              "criteria":{  
                 "narrative":"After academy training, firefighters need to complete an apprenticeship of up to four years. Some fire departments send students for additional education with the National Fire Academy, where they learn disaster preparedness, public education and how to handle hazardous materials"
              },
              "issuer":{  
                 "id":"https://erasmusdev.gluu.org",
                 "type":"Profile",
                 "name":"Erasmus",
                 "url":"https://erasmusdev.gluu.org",
                 "email":"erasmussupport@gluu.org",
                 "verification":{  
                    "allowedOrigins":"https://erasmusdev.gluu.org",
                    "type":"hosted"
                 }
              }
           }
        }
        ```  
        
        Notification will be sent to `Asserter` indicating that his/her badge verified successfully or not with the response of above API.
    
### (11) Temporary and permanent badge link
`Temporary link will be generated for both public and private badges but permanent badge link will be generated for public badges only which has no expiration.`

For example,

Temporary badge link: `https://erasmusdev.gluu.org/badge-mgr/tmp/764473bf`

Permanent badge link: `https://erasmusdev.gluu.org/badge-mgr/badges/verify/33fc298b-dd11-4816-9f4f-afcd11bfb218`

Both links will be used to verify badge and provides badge information after successful verification.

### (12) Grant and revoke badge access to FIDES
Badge Manager provides facility to grant and revoke badge(actual badge) access to FIDES.

As soon as badge request get approved and badge entry get persisted in LDAP, badge access is granted to FIDES and FIDES can access badge information by calling Bagde Manager API.

FIDES will be able to access badge information until badge get verified by the verifier in mobile app.
 
As soon as badge get verified by verifier in mobile app, Badge manager will revoke badge access to FIDES and FIDES will no longer access that badge.

`Note: This functionality is available for Approved badge requests only as badge entry get persisted in LDAP once badge request get approved` 
 
1. Exposing API to return approved badge requests for participant

    API Endpoint: `/badges/request/list/{participant}/{status}`
        
    Method: `GET`
        
    Params: `/participant/status`
        
    Request: `https://erasmusdev.gluu.org/badge-mgr/badges/request/list/5923f1de8bc19a19d7e939c3/approved`
    
    Response:
    ```json
    {
      "badgeRequests": [
        {
          "inum": "@!4301.2A50.9A09.7688!1002!DFA7.9B12",
          "participant": "5923f1de8bc19a19d7e939c3",
          "templateBadgeId": "58e498230cd5268169f75761",
          "templateBadgeTitle": "Emergency Medical Technician-Basic",
          "status": "Approved",
          "requesterEmail": "megtest3@gluu.org",
          "privacy": "Public"
        },
        {
          "inum": "@!4301.2A50.9A09.7688!1002!EEB5.6D1F",
          "participant": "5923f1de8bc19a19d7e939c3",
          "templateBadgeId": "58e49bec0cd5268169f7576a",
          "templateBadgeTitle": "Entry-Level Firefighter",
          "status": "Approved",
          "requesterEmail": "megtest3@gluu.org",
          "privacy": "Public"
        }
      ],
      "error": false
    }
    ```

2. Exposing API to return badge data

    API Endpoint: `/badges/{badgeRequestInum}`
        
    Method: `GET`
        
    Params: `badgeRequestInum`
        
    Request: `https://erasmusdev.gluu.org/badge-mgr/badges/%40!4301.2A50.9A09.7688!1002!DFA7.9B12`
    
    Response: 
    ```json
    {
      "context": "https://w3id.org/openbadges/v2",
      "id": "https://erasmusdev.gluu.org/badge-mgr/badges/verify/3cb3f16f-b599-4741-879b-ec506ea70eb4",
      "type": "Assertion",
      "issuedOn": "Mon May 29 13:10:14 UTC 2017",
      "expires": "",
      "recipient": {
        "type": "text",
        "identity": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI4MmQ2NzAzZC0zNzFmLTQ0NTUtYjNhMS05ZjJiODRmMzY3NjUiLCJzdWIiOiJ0ZXN0IiwiaXNzIjoiaHR0cDovL3d3dy5nbHV1Lm9yZyIsInVzZXJpbmZvIjoie1wiZW1haWxcIjpcInRlc3RfdXNlckB0ZXN0Lm9yZ1wiLFwiZW1haWxfdmVyaWZpZWRcIjpcInRydWVcIixcInN1YlwiOlwiSzZzQmpRa1pRbDNSUC1YSUxhMWdMYTJrMjExenY0QmdvVkpDdHZmUlpqQVwiLFwiem9uZWluZm9cIjpcIkFtZXJpY2EvQ2hpY2Fnb1wiLFwibmlja25hbWVcIjpcInVzZXJcIixcIndlYnNpdGVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmdcIixcIm1pZGRsZV9uYW1lXCI6XCJVc2VyXCIsXCJsb2NhbGVcIjpcImVuLVVTXCIsXCJwcmVmZXJyZWRfdXNlcm5hbWVcIjpcInVzZXJcIixcImdpdmVuX25hbWVcIjpcIlRlc3RcIixcInBpY3R1cmVcIjpcImh0dHA6Ly93d3cuZ2x1dS5vcmcvd3AtY29udGVudC91cGxvYWRzLzIwMTIvMDQvbWlrZTMucG5nXCIsXCJ1cGRhdGVkX2F0XCI6XCIyMDE3MDIyNDEyNTkxNS41MzhaXCIsXCJuYW1lXCI6XCJveEF1dGggVGVzdCBVc2VyXCIsXCJiaXJ0aGRhdGVcIjpcIjE5ODMtMS02XCIsXCJmYW1pbHlfbmFtZVwiOlwiVXNlclwiLFwiZ2VuZGVyXCI6XCJNYWxlXCIsXCJwcm9maWxlXCI6XCJodHRwOi8vd3d3Lm15d2Vic2l0ZS5jb20vcHJvZmlsZVwifSIsImlhdCI6MTQ5NjA2MzQxNH0.9D3FW8Ze9SHtbU7ywof6yPjh4EHfiUAcTvC8tc_LHg38T9AXqXPi5B2GtJb2c65Q2pLSZqhHjf5DObnOG0mG5A"
      },
      "verification": {
        "type": "hosted"
      },
      "badge": {
        "type": "BadgeClass",
        "id": "https://erasmusdev.gluu.org/badge-mgr/badgeClass/e5216b6f-870e-4571-ac18-f97b46b577eb?key=YCBCZP73X5SG",
        "name": "Emergency Medical Technician-Basic",
        "description": "EMT-Basic training requires about 100 hours of instruction, including practice in a hospital or ambulance",
        "image": "https://erasmus.gluu.org:8000/images/badges/7847e312-ccf3-4a3f-b516-433d7da7db63.png",
        "criteria": {
          "narrative": "EMT-Basic students must pass an exam testing the ability to assess patient condition, handle trauma or cardiac emergencies and clear blocked airways. They also learn to immobilize injured patients and give oxygen."
        },
        "issuer": {
          "id": "https://erasmusdev.gluu.org",
          "type": "Profile",
          "name": "Erasmus",
          "url": "https://erasmusdev.gluu.org",
          "email": "erasmussupport@gluu.org",
          "verification": {
            "allowedOrigins": "https://erasmusdev.gluu.org",
            "type": "hosted"
          }
        }
      }
    }
    ```
 
#### Important note
- APIs which are exposing to mobile app will require `AccessToken` as a header which is the access token retrieved using app auth in mobile app and this access token will be used in Badge Manager to verify user and retrieving user info using oxd. 
- APIs which are exposing to FIDES are secured with static access token(for now).
  