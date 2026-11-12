# Rentanora backend — persistent project context

Last updated: 2026-11-12 11:10 IST (UTC+05:30; approximate screenshot time).

## Read this first

This is a backend-only handoff for continuing work between ChatGPT, Gemini, or another assistant. It combines source inspection on November 12, 2026 with explicitly dated observations supplied by the user. It is not a live AWS inventory. Recheck relevant files and infrastructure before assuming recorded state is still current.

**The immediate next step is to confirm the current build result, then implement the missing OTP orchestration and server-side signup enforcement.** The email sender exists in source, but mandatory email-plus-phone verification is not yet an end-to-end working backend feature.

No application code was changed during creation of this document. No build, AWS mutation, commit, or push was performed by the documenting assistant.

## 1. Scope and product

Rentanora is a general rental marketplace. Its intended scope includes furniture, electronics, appliances, clothing and accessories, tools and equipment, vehicles, homes, and spaces. Do not describe it as exclusively a clothing rental service.

Implemented backend features visible in source: health check, Cognito password login, access-token validation, current-user endpoint, and a direct Cognito signup endpoint. A substantial set of OTP-related models, storage helpers, cryptographic helpers, and an SES email sender exists, but the coordinating API flow is missing.

Planned or incomplete backend features: pre-account verification of both email and phone, delivery and verification endpoints, protected verification-session transport, resend and attempt enforcement, SMS sending, atomic final signup gating, and retry-safe account creation. Listing/category UI does not establish that listing CRUD, rentals, payments, availability, or bookings exist in this backend; no such controllers were found in the inspected Java source inventory.

## 2. Project locations and ownership boundaries

- Backend root: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend`
- This file: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/RENTANORA_CONTEXT.md`
- Frontend root: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraUI`
- Suggested frontend handoff location: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraUI/RENTANORA_CONTEXT.md`. **Unverified:** whether that separate file exists; the user intends to create frontend context separately.
- Backend repository, from prior push output: `https://github.com/chitranshibendale/RentanoraBackend`; branch `main`.

Frontend high-level context only: React/Vite UI calls backend APIs. Public site is `https://rentanora.com/`; historical deployment uses S3/CloudFront with API traffic forwarded to API Gateway and Lambda. Keep frontend component details in its own context document. The frontend was deliberately not inspected for this backend-only handoff.

## 3. User working preferences

- Do not modify application code unless the user explicitly authorizes it. Normally provide copy-paste changes for the user to apply in IntelliJ.
- Always provide the complete absolute path, including the folder in which a new file must be created.
- Identify exactly what old code to replace, or provide complete file content.
- Work one logical step at a time and wait for “next”/“nxt.” Group closely related edits when needed to keep the application buildable and avoid unnecessary repeated builds or commits.
- Put request/data/configuration models in the `model` folder, normally a feature subfolder such as `model/verification` or `model/auth`.
- Put new related code in feature folders such as `service/verification`, `repository/verification`, and `mapper/verification`.
- Keep reusable messages, paths, attribute names, and configuration in constants/configuration files.
- Use Lombok `@Builder` where appropriate; do not invent handwritten builders. Some existing files still use constructors or wildcard imports; that is observed legacy code, not permission to disregard the preference.
- Use explicit normal and static imports, not wildcard imports.
- Provide complete Git commands when requested: repository `cd`, `git status`, `git add .`, concrete `git commit -m`, `git pull --rebase origin main`, and `git push origin main` where appropriate. Stop on failure. Do not automatically commit or push.
- Infrastructure should be created/maintained from code and the pipeline. Console inspection is acceptable; Hostinger DNS records were manually entered because DNS is hosted there.
- Do not claim pasted suggestions were implemented or tested without source or result evidence.
- No secret values, passwords, access/refresh tokens, AWS credentials, actual OTP values, or raw verification-session tokens belong in this file or logs.

## 4. Verified backend architecture

Source inspected: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/pom.xml`.

- Java 21; Maven wrapper (`mvnw`) and Maven project `com.rentanora:rentanora-backend:0.0.1-SNAPSHOT`.
- Spring Boot parent **4.1.2** as recorded in the actual POM. Preserve actual versions unless an intentional version change is requested.
- Spring MVC, Spring Security, OAuth2 resource-server JWT support, Jakarta validation.
- AWS SDK for Java v2 **2.55.14** dependencies: Cognito Identity Provider, SES v2, SNS, DynamoDB, Secrets Manager.
- Lombok **1.18.48**, provided scope.
- AWS serverless Java container for Spring Boot 4 **3.0.5**.
- Maven assembly plugin **3.8.0**, profile `lambda`.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/assembly/lambda.xml` packages compiled classes at ZIP root and runtime dependencies under `lib`, excluding embedded Tomcat dependencies.
- Lambda package: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/target/rentanora-backend-0.0.1-SNAPSHOT-lambda.zip`.
- Application class: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/RentanoraBackendApplication.java`.
- Handler source: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/handlers/StreamLambdaHandler.java`, extending `SpringDelegatingLambdaContainerHandler` and passing the application class to its constructor. **Unverified:** current deployed Lambda handler setting; inspect before changing it.

Historical request path confirmed by user testing: browser → CloudFront `/api/*` behavior → API Gateway REST API → Java Lambda → Cognito/AWS services. API Gateway REST proxy event shape matters; earlier non-proxy integration caused a null-path exception. Exact current API routes and CloudFront behavior are not defined by the inspected verification template; inspect AWS before assuming new paths are already routed.

## 5. Existing authentication and important enforcement gap

Files:

- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/auth/AuthController.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/auth/AuthService.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/auth/CognitoSecretHash.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/auth/AuthExceptionHandler.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/auth/LoginRequest.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/config/CognitoConfig.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/config/CognitoJwtConfig.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/config/SecurityConfig.java`
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/constants/AuthConstants.java`

### Actual endpoints

| Endpoint | Source behavior |
|---|---|
| `GET /api/health` | Anonymous health check; historically returns status UP. |
| `POST /api/auth/login` | Validated email/password input; calls Cognito `InitiateAuth` with `USER_PASSWORD_AUTH` and client secret hash. Returns access token and expiry with no-store caching. Additional challenge without authentication result yields conflict. |
| `GET /api/auth/me` | Requires JWT authentication; returns `UserResponse(userId)` using principal name. |
| `POST /api/auth/signup` | Validates signup fields and phone syntax, directly calls Cognito `signUp`, returns 201 `RegistrationResponse(confirmed, deliveryDestination)` with no-store caching. **Does not check the new OTP sessions or verified contacts.** |
| `POST /api/auth/confirm-signup` | Path constant and anonymous security rule exist, plus request model; no corresponding controller method found. |
| `POST /api/auth/resend-confirmation` | Path constant and anonymous security rule exist, plus request model; no corresponding controller method found. |

`AuthService.authenticateUser(LoginRequest)` also exists, fetching Cognito `GetUser` after authentication and extracting `sub`; the current login controller calls `authenticate` directly.

`CognitoConfig` deliberately uses `AnonymousCredentialsProvider` for the existing public Cognito operations. It is not suitable for IAM-authorized Cognito admin operations. An eventual admin provisioning flow needs a separately configured default-credentials client and scoped runtime IAM permission.

JWT decoder verifies issuer, token use `access`, matching `client_id`, nonblank subject, and required expiration alongside standard validation. It fetches Cognito JWKS. Do not switch to accepting ID tokens without an explicit design change.

`SecurityConfig` is stateless, disables CSRF/form login/HTTP Basic/logout/request cache, and configures `sessionFixation(...none())` for the Lambda adapter. Only health and listed auth POSTs are public; all other requests require authentication. New verification endpoints are not currently permitted or implemented. If verification moves to cookies, revisit CSRF/origin protections; the present CSRF-disabled bearer-token setup is not automatically adequate for a cookie-authenticated verification flow.

`AuthExceptionHandler` maps validation, invalid credentials, Cognito errors, and `SdkClientException` to generic `ProblemDetail` messages. Dedicated SES/DynamoDB/Secrets Manager service-exception handling and verification error behavior still need design/testing.

### Signup data and Cognito schema

`/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/model/auth/SignUpRequest.java` contains:

- firstName and lastName: required, max 100;
- middleName: optional, max 100;
- phone: required, max 32; service checks international `+` and digits syntax;
- email: required, Jakarta `@Email`, max 254; serves as username;
- city/state/country: required, max 50 each;
- pincode: required, max 16;
- password: required, max 256; Cognito password policy applies separately.

Actual attribute names from `AuthConstants`, including exact case:

| Data | Cognito attribute |
|---|---|
| Email | `email` |
| First/middle/last name | `given_name`, `middle_name`, `family_name` |
| Phone | `phone_number` |
| City | `custom:city` |
| State | `custom:State` |
| Country | `custom:Country` |
| Postal code | `custom:pincode` |

Do not silently rename these custom attributes: previously created Cognito custom attributes cannot simply be renamed/deleted. Actual live schema beyond earlier screenshots is **Unverified** until queried.

Other auth models under `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/model/auth/`: `ConfirmSignUpRequest`, `ResendConfirmationRequest`, `RegistrationResponse`, `LoginResponse`, and `UserResponse`. Sensitive request/token `toString` methods are redacted; preserve that behavior.

## 6. Mandatory contact verification: target contract versus current code

**User requirement:** email ownership AND phone ownership must be verified before final account creation. Intended flow is UI → backend → AWS, with all authorization decisions enforced by the backend. Disabling the UI button is insufficient. Regex/phone syntax checks are not proof of ownership.

UI behavior, from conversation only (not inspected in this backend handoff): Verify beside each contact field opens a modal and immediately requests a code. There is no separate Send Code button. The modal accepts a six-digit code and has a resend button enabled after approximately 60 seconds. Changing a destination must invalidate its verification. Phone lengths vary internationally; do not enforce a universal ten-digit rule. Frontend API wiring and actual verification success are incomplete/unconfirmed; refer to frontend context for current implementation.

### Existing configuration

`/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/resources/application.properties`:

| Property | Value / environment source |
|---|---|
| `aws.cognito.region` | `us-east-1` |
| `aws.cognito.user-pool-id` | `${COGNITO_USER_POOL_ID}` |
| `aws.cognito.client-id` | `${COGNITO_CLIENT_ID}` |
| `aws.cognito.client-secret` | `${COGNITO_CLIENT_SECRET}` — name only, never record value |
| `aws.verification.region` | `${AWS_REGION:us-east-1}` |
| `aws.verification.table-name` | `${CONTACT_VERIFICATION_TABLE:rentanora-contact-verification}` |
| `aws.verification.hash-secret-id` | `${CONTACT_VERIFICATION_HASH_SECRET_ID:rentanora-contact-verification-otp-key}` |
| `aws.verification.email.from-address` | `${VERIFICATION_EMAIL_FROM:no-reply@rentanora.com}` |
| `verification.code-length` | 6 |
| `verification.code-expiry-seconds` | 600 |
| `verification.resend-cooldown-seconds` | 60 |
| `verification.max-attempts` | 5 |
| `verification.session-expiry-seconds` | 1800 |

`/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/config/VerificationConfig.java` registers `VerificationProperties`, `VerificationStorageProperties`, and `VerificationEmailProperties`; creates closeable DynamoDB, Secrets Manager, SES v2 clients using the default AWS credentials chain and configured region; exposes UTC `Clock`. Merely instantiating these clients does not send a message. Secret retrieval is lazy.

### Models — actual source inventory

All are in `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/model/verification/`:

| File | Actual content |
|---|---|
| `VerificationProperties.java` | Validated record for code length, expiry, cooldown, max attempts, session expiry. Code length fixed to 6; expiry 60–900 seconds; max attempts 1–5; session expiry 600–3600 seconds. |
| `VerificationStorageProperties.java` | Validated region, tableName, hashSecretId configuration. |
| `VerificationEmailProperties.java` | Validated `fromAddress`, prefix `aws.verification.email`. |
| `VerificationChannel.java` | EMAIL/PHONE enum; JSON strings `email`/`phone`. |
| `VerificationStatus.java` | PENDING_DELIVERY, CODE_SENT, DELIVERY_FAILED, VERIFIED, LOCKED. These names exist; transitions are not implemented yet. |
| `VerificationSession.java` | Lombok `@Builder(toBuilder=true)` record: id, expiresAt, consumed. Redacted `toString`. |
| `VerificationChallenge.java` | Lombok `@Builder(toBuilder=true)` record: id, sessionId, challengeId, channel, destination, codeHash, status, attempts, codeExpiresAt, resendAvailableAt, expiresAt. Redacted `toString`. |
| `SendVerificationCodeRequest.java` | Required channel and nonblank destination, max 254. |
| `VerifyCodeRequest.java` | Required channel/destination and six-digit code validation. |
| `SendVerificationCodeResponse.java` | channel, expiresInSeconds, resendAfterSeconds; no raw code. |
| `VerifyCodeResponse.java` | channel, verified; must only be true after server checks once integrated. |

### Services — existing, not yet orchestrated

All are in `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/service/verification/`:

| File / important signature | Actual behavior and limits |
|---|---|
| `VerificationCodeGenerator.java`: `generate()` | Uses SecureRandom to generate the configured number of decimal digits; preserves leading zeros. |
| `VerificationTokenGenerator.java`: `generate()` | 32 random bytes, URL-safe Base64 without padding, 43 characters. |
| `VerificationSecretProvider.java`: `getKey()` | Lazily fetches secret string from Secrets Manager, requires minimum length 64, constructs HMAC-SHA256 key, clears temporary byte array, caches key for this application instance. No rotation/version management implemented. |
| `VerificationCodeHasher.java`: `hash(sessionId, challengeId, channel, destination, code)` and `matches(expectedHash, same fields...)` | HMAC-SHA256 using length-prefixed UTF-8 fields, outputs hex. Binds code to session, challenge generation, channel, and destination. Uses MessageDigest.isEqual for hash comparison. Raw OTP is not stored by this helper. |
| `VerificationRecordKeyFactory.java`: `sessionRecordId(token)` | SHA-256 hashes the high-entropy session token; prefixes `session#`. Do not use bare SHA-256 for low-entropy OTPs. |
| Same file: `challengeRecordId(sessionId, channel)` | Stable key `challenge#` + session ID + `#` + channel enum name. One current record per channel/session. |
| `VerificationSessionService.java`: `createSession()` | Creates a fixed-expiry session, consumed=false; tries three collision-safe conditional inserts and returns raw token. No HTTP transport/cookie integration yet. |
| Same file: `findActive(token)` | Rejects null/wrong-length token, hashes it, strongly reads session, filters consumed and expired sessions using Clock. Not an atomic authorization guard for later writes. |
| `VerificationContactNormalizer.java`: `normalize(channel, destination)` | Strips outer whitespace; basic email regex and length 254; international phone syntax `+` followed by digits, max 15 digits. Preserves email capitalization. Does not establish deliverability or ownership; not full international phone validation. |
| `VerificationChallengeFactory.java`: `create(session, channel, destination, code)` | Rejects missing/expired/consumed session snapshot, normalizes contact, generates new challenge ID, hashes code, builds PENDING_DELIVERY challenge, attempts=0. Code expiry is min(now+600, session expiry); cooldown now+60; record TTL equals session expiry. Does not save or send. |
| `VerificationEmailSender.java`: `send(destination, code)` | Requires six-digit format, normalizes email, creates plain-text SES SendEmail request from configured address. Returns void; AWS failures propagate. No logging of OTP. SES acceptance does not prove recipient delivery or ownership. No caller/orchestrator yet. |

Constants:

- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/constants/VerificationConstants.java`: proposed `/api/auth/verification/send-code` and `/api/auth/verification/verify-code` paths, messages, patterns, HMAC algorithm, key minimum length. **Path constants do not mean endpoints exist.**
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/constants/VerificationStorageConstants.java`: field names, key prefixes, expected-value aliases and conditional-write expressions.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/constants/VerificationEmailConstants.java`: subject/body strings for plain-text verification email. Body does not hardcode an expiry that could disagree with remaining session lifetime.

### Mappers and repositories

- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/mapper/verification/VerificationSessionMapper.java`: maps id, numeric expiry, boolean consumed to/from DynamoDB attributes; malformed fields fail closed.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/mapper/verification/VerificationChallengeMapper.java`: maps all challenge fields; enum names stored uppercase; epoch values numeric. Actual inspected `fromItem` uses `new VerificationChallenge(...)`, despite the preference for builder mapping. It also retains a wildcard static import. Record this accurately; do not silently refactor during documentation.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/repository/verification/VerificationSessionRepository.java`: `findById(String)` strongly consistent GetItem; `createIfAbsent(VerificationSession)` conditional PutItem with attribute_not_exists(id). Conditional conflict returns false; other AWS failures propagate. No consume method.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/main/java/com/rentanora/backend/repository/verification/VerificationChallengeRepository.java`: same read/create methods; `replaceIfUnchanged(expected, updated, now)` checks equal record ID/session/channel/TTL then conditional PutItem matching expected challengeId/status/attempts and expiresAt > now. Concurrent conflict returns false.

### Critical unfinished integration and security requirements

1. No verification controller or orchestration service was found. No public OTP endpoint is currently working.
2. Need issuance flow: validate destination and active session, enforce persistent cooldown/abuse limits, generate code, atomically claim challenge, deliver, conditionally update outcome only for the same challenge generation.
3. Need verification flow: enforce status, exact contact/session binding, expiry and max attempts; atomically count incorrect attempts; reject old codes after resend; conditionally mark verified.
4. Existing challenge CAS checks do not atomically check the session has not been consumed. A pre-read `findActive` is not sufficient under concurrency. Add session condition checks in transaction-safe challenge/final-signup writes.
5. Need final registration gate checking BOTH verified contacts match submitted email and phone, session is current/unconsumed, and proofs cannot be replayed. Direct `AuthService.register` presently bypasses all of this.
6. Define idempotency/recovery for Cognito account creation versus DynamoDB session consumption; AWS calls and DynamoDB writes do not form one distributed transaction. No final design selected. AdminCreateUser/SUPPRESS plus permanent password was discussed as a possible option, not implemented/approved as the definitive design.
7. Need safe session token transport (e.g. HttpOnly/Secure/SameSite cookie with explicit local-dev handling) and corresponding CSRF/origin controls. Do not put secrets in browser code or log raw session tokens.
8. Need persistent per-destination and appropriately trusted per-source abuse limits before exposing email/SMS sending publicly. UI resend timers and per-session cooldown alone do not stop creating many sessions. Limits must survive Lambda concurrency/restarts.
9. TTL cleanup is asynchronous; always check timestamps explicitly. Attempt/cooldown values currently exist in models/configuration but are not enforced by an end-to-end service.
10. Default email case handling and Cognito username case settings require alignment. Normalizer preserves case; do not lowercase everything without inspecting pool behavior.
11. SMS is not implemented. SNS dependency alone does not establish a configured SMS channel. Provider API choice, sandbox, supported countries, origination/registration, limits and delivery requirements remain unverified. Do not promise worldwide SMS delivery from current code.
12. No meaningful OTP state/concurrency tests were found. Add tests for HMAC bindings, expiration, resends, attempts, stale writes, delivery failures and signup bypass prevention before declaring the feature ready.

## 7. AWS resource identifiers and observed state

Account: `070389302498`; region: `us-east-1` (N. Virginia). Local CLI binary: `/usr/local/bin/aws`; local profile: `rentanora`. Credentials themselves must never be saved here.

| Resource | Recorded identifier |
|---|---|
| Lambda | `rentanora-backend` |
| Runtime execution role | `arn:aws:iam::070389302498:role/service-role/rentanora-backend-role-0x4cy0pk` |
| GitHub deployment role | `arn:aws:iam::070389302498:role/GitHubActionsRentanoraBackendDeploy` |
| CloudFormation stack | `rentanora-contact-verification` |
| DynamoDB table | `rentanora-contact-verification`; user confirmed ACTIVE on Nov 11; current live status unqueried in this handoff |
| HMAC secret name | `rentanora-contact-verification-otp-key` |
| HMAC secret ARN (identifier only) | `arn:aws:secretsmanager:us-east-1:070389302498:secret:rentanora-contact-verification-otp-key-d14pdV` |
| SES identity | `rentanora.com`, region us-east-1 |
| Default email From address | `no-reply@rentanora.com`; source default, not proof of verified sending or an existing inbox |
| Cognito pool, historical console evidence | `us-east-1_tNco83hFo`; current environment value unverified |
| API Gateway REST API, historical evidence | `RentanoraBackendApi`, ID `gaqiq5r6pi`, stage `prod`; inspect live routing before adding verification paths |

GitHub deployment role trust, from earlier user CLI output: federated GitHub provider in this account, audience `sts.amazonaws.com`, subject exactly `repo:chitranshibendale@59919512/RentanoraBackend@1450613369:ref:refs/heads/main`. This is an observed ID-qualified subject; do not replace it with the conventional non-ID-qualified form just because it looks unusual.

Role distinction: GitHub role assumes OIDC to deploy infrastructure and Lambda code. Lambda role is assumed at runtime to read/write verification records, read the HMAC secret, and send mail. Runtime SDK clients use that role through default credentials. Do not use the Lambda role ARN as the GitHub role variable or give deployment credentials to the frontend.

### Infrastructure source and IAM application

- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/infrastructure/contact-verification.yaml`: SES EmailIdentity; Secrets Manager generated HMAC key; PAY_PER_REQUEST DynamoDB table with string hash key `id` and TTL attribute `expiresAt`; all three resources have Retain deletion/replacement policies. Contains table/secret outputs and six DKIM name/value outputs. Retain means removing the stack does not necessarily remove resources.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/infrastructure/deploy-verification-policy.json`: inline policy source named `RentanoraContactVerificationDeployment`, applied manually to deployment role. Includes stack operations (notably GetTemplateSummary), template validation, scoped table lifecycle/TTL/tag operations, scoped secret management, GetRandomPassword, and SES identity management scoped to `rentanora.com`.
- `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/infrastructure/runtime-verification-policy.json`: inline policy source named `RentanoraContactVerificationRuntime`, applied manually to runtime role. Allows table GetItem/PutItem/UpdateItem/DeleteItem/ConditionCheckItem, GetSecretValue on exact secret ARN, and ses:SendEmail for domain identity with StringEquals ses:FromAddress = no-reply@rentanora.com.

These JSON files are versioned policy documents, not IAM resources managed by the current CloudFormation stack. The workflow does not automatically apply their edits. User successfully applied earlier policies by CLI; latest SES runtime addition is present locally, but a response “next” after the command is not an independently verified live IAM readback. Do not claim live attachment contents were audited today.

### SES / DNS evidence, last reported November 12 early morning IST

- `get-email-identity` user output: VerifiedForSendingStatus false, DKIM status PENDING. Exact command execution time was not included; surrounding console screenshots were about 01:37–01:41 IST.
- Console last DNS check shown was November 12 **00:51 UTC+05:30**, meaning **00:51 IST (12:51 AM)**, not UTC. It reported an earlier DNS lookup error. Do not treat this old last-check time as proof DNS is currently broken.
- User queried all three CNAME records publicly, then directly against BOTH authoritative Hostinger servers `atlas.dns-parking.com` and `hyperion.dns-parking.com`; all returned the expected targets. Reported before approximately 01:41 IST. Live state has not been rechecked during this documentation task.
- `get-account` user output: ProductionAccessEnabled false, SendingEnabled true. Screenshot about 01:46–01:47 IST showed sandbox in us-east-1 and a disabled production-access request button labeled Domain verification needed.
- Account sending enabled does NOT mean the domain is verified or arbitrary signup addresses can receive email. Domain verification and production approval are separate.
- **Latest domain identity status: Verified**, shown in the SES console screenshot at approximately 11:10 IST on 2026-11-12. The screenshot does not independently show the DKIM configuration status. Production access was **false/sandbox** in the earlier account check around 01:46 IST; its current state is **Unverified**. Domain verification does not establish production access.
- No OTP email delivery success has been shown. No SMS sending success has been shown.

DNS provider is Hostinger. Record TTL entered as 300 seconds. Exact public DKIM pairs from stack outputs and matching dig results:

| Full CNAME name | Target |
|---|---|
| `tez4e5x7whhhz3xs7qmvh535noq46dww._domainkey.rentanora.com` | `tez4e5x7whhhz3xs7qmvh535noq46dww.dkim.amazonses.com` |
| `osmxsrat565rzm4grwozjymjs2xpv32f._domainkey.rentanora.com` | `osmxsrat565rzm4grwozjymjs2xpv32f.dkim.amazonses.com` |
| `sdtisa323hlzzcpogivogm3vcx7ctihk._domainkey.rentanora.com` | `sdtisa323hlzzcpogivogm3vcx7ctihk.dkim.amazonses.com` |

These are public DNS values, not private signing keys. Hostinger automatically appends the domain in its Name field, so the user entered selector + `._domainkey`. Preserve existing unrelated website/mail DNS records.

Earlier guidance said look for Retry in the SES DKIM section, but the actual console screenshot had no Retry button. This was corrected: do not repeatedly direct the user to a nonexistent button, delete/recreate the identity, or toggle DKIM just to refresh status. Refresh reloads displayed status; SES performs its own detection. The console notice says detection may take up to 72 hours. Use current AWS docs if troubleshooting beyond that interval.

## 8. Deployment pipeline and repository evidence

Workflow: `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/.github/workflows/deploy.yml`.

- Push to main or workflow_dispatch; contents:read globally; serialized production concurrency with cancel-in-progress:false.
- Build job uses checkout@v7 with persist-credentials:false; setup-java@v6, Corretto 21, Maven cache; runs tests/package with lambda profile and dummy Cognito configuration. Upload-artifact@v7 stores lambda-package ZIP, retention one day, no added compression.
- Deploy job has contents:read and id-token:write; its OWN checkout loads infrastructure files. download-artifact@v8 downloads package under package/.
- configure-aws-credentials@v6.3.0 assumes `${{ vars.AWS_ROLE_ARN }}`, region `${{ vars.AWS_REGION }}`, allowed-account-ids 070389302498.
- GitHub repository variables required: AWS_ROLE_ARN (deployment role above), AWS_REGION (us-east-1), LAMBDA_FUNCTION_NAME (rentanora-backend).
- Validates then deploys the CloudFormation stack, with explicit region us-east-1 and no-fail-on-empty-changeset. Updates existing Lambda function code, waits with function-updated-v2, prints limited function status.
- Workflow does not create the existing Cognito pool/API Gateway/CloudFront/Lambda, update Lambda environment variables, apply local IAM JSON policies, or automatically configure SMS/SES production access.

Source inspection observed clean Git status BEFORE writing this document. Latest local commits:

| Commit | Recorded author timestamp | Message |
|---|---|---|
| `921dfbf` | 2026-11-12 02:02:19 +05:30 | Adding verification email sender |
| `ae6ad05` | 2026-11-12 00:21:29 +05:30 | Fix CloudFormation DKIM output indentation |
| `b7486f3` | 2026-11-12 00:18:23 +05:30 | Add verification services and SES domain infrastructure |

User screenshot explicitly confirmed successful build/deploy for `ae6ad05`, run `43081731487`, shown around 01:24 IST. **Unverified:** remote push/deployment outcome for latest local commit `921dfbf`; no matching run evidence inspected. The documentation task did not fetch Git or query GitHub.

## 9. Validation: evidence and commands

### What is actually confirmed

- User provided earlier BUILD SUCCESS outputs and successful deployment screenshots; do not generalize these to every later change.
- CloudFormation validate-template succeeded after DKIM Outputs indentation fix; user pasted Parameters/Description JSON.
- User screenshot confirmed ae6ad05 workflow green.
- Local report `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/target/surefire-reports/com.rentanora.backend.RentanoraBackendApplicationTests.txt` exists: **1 test, 0 failures, 0 errors, 0 skipped**, file modification time **2026-11-12 10:16:40 IST**.
- Lambda ZIP exists with modification time **2026-11-12 10:16:41 IST**. These are observed existing artifacts; the documenting assistant did not run that build or see its complete console output.
- Only discovered test source is `/Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend/src/test/java/com/rentanora/backend/RentanoraBackendApplicationTests.java`, a SpringBootTest contextLoads smoke test. Passing it does not test AWS delivery, database concurrency, API correctness or final signup verification.
- Most recent assistant-requested build had not received a new user-pasted result before context creation; the existing newer report is separate evidence as described above.

### Build command (dummy local/CI Cognito settings, never real credentials)

```bash
cd /Users/chitranshibendale/Desktop/CB_Rentanora/RentanoraBackend

COGNITO_USER_POOL_ID=us-east-1_test \
COGNITO_CLIENT_ID=ci-test-client \
COGNITO_CLIENT_SECRET=ci-test-secret \
bash ./mvnw --batch-mode --no-transfer-progress -Plambda clean package
```

Missing Cognito environment placeholders previously caused context startup failures. Dummy values above are intentionally non-secret and allow configuration binding. This command builds/tests/packages; it does not intentionally send an OTP. Revisit if future startup logic introduces AWS calls. Do not run tests or builds during documentation-only work without need: they write target artifacts.

### Read-only AWS checks the USER can run

```bash
/usr/local/bin/aws sesv2 get-email-identity \
  --email-identity rentanora.com \
  --profile rentanora \
  --region us-east-1 \
  --query '{SendingEnabled:VerifiedForSendingStatus,DKIMStatus:DkimAttributes.Status}' \
  --no-cli-pager

/usr/local/bin/aws sesv2 get-account \
  --profile rentanora \
  --region us-east-1 \
  --query '{ProductionAccess:ProductionAccessEnabled,SendingEnabled:SendingEnabled}' \
  --no-cli-pager
```

If local CLI authentication expires, the previously working login form was:

```bash
/usr/local/bin/aws login --profile rentanora --region us-east-1
```

Copy commands without terminal prompts. Do not paste credentials into chat. No live AWS checks were run as part of writing this file.

### Resolved troubleshooting history

- `aws: command not found`: installed CLI and used absolute `/usr/local/bin/aws`. Missing leading slash and pasted terminal prompts caused separate shell errors.
- Expired CLI session: login with the correct profile/region.
- Deployment could not load infrastructure/contact-verification.yaml: deploy job needed its own checkout; build checkout does not carry over to another job.
- CloudFormation GetTemplateSummary AccessDenied: action added to deployment policy and applied via IAM CLI. Local file edits alone do not update IAM.
- Invalid outputs property for six DKIM names: outputs were nested under VerificationTableArn. Align each DKIM output as a sibling under Outputs. Validation and subsequent deployment succeeded.
- PAT rejected workflow update: token needed workflow-update permission (classic workflow scope or appropriate fine-grained Workflows write). User previously fixed authentication. Never save PAT values here.
- Earlier API Gateway test returned a Java null-path error: request event shape/integration mismatch; later health endpoint succeeded through API Gateway and public domain.
- Earlier login succeeded but /me failed with adapter/session exception: stateless security/session-fixation change resolved user-observed flow. Keep current security configuration unless reviewing actual error evidence.

## 10. Current handoff and next actions

### Latest completed work

Source inspection confirms email configuration model, SES client bean, email constants/sender, and SES sending statement in runtime policy file are present. Local commit 921dfbf includes email-sender work. A passing context-load test report and ZIP from 10:16 IST are available. Last explicitly confirmed deployment in conversation is ae6ad05. Domain and sandbox state have not been checked again since the user's early-morning outputs.

### Immediate pending action

Confirm the current build result with the user (or, with authorization, run the documented build), using the existing report only as the limited evidence it is. Then proceed with the missing orchestration; do not restart already completed setup or claim OTP delivery works.

### Recommended next implementation order

1. Inspect source again for changes since this timestamp. Confirm build/package of the email additions; clarify live runtime policy if email delivery will be tested.
2. Check SES production access separately; the domain identity is now verified. Public recipient delivery needs production access; sandbox tests require permitted verified recipients.
3. Implement transaction-safe session/challenge state transitions, including active-session condition checks, resend generation, persistent abuse limits and attempt enforcement.
4. Connect session creation → code generation → challenge claim → email delivery → conditional outcome updates. Include safe error behavior; never return/log raw codes.
5. Implement code submission and verification for the exact contact/session/current challenge. Add focused expiry, wrong-code, lockout, stale resend and concurrency tests.
6. Choose/configure SMS sending, origination and country support; add scoped runtime permissions and an SMS sender. All live SMS setup is currently Unverified.
7. Implement replay-resistant final signup requiring both verified contacts, with Cognito/DynamoDB idempotency and partial-failure recovery. Close the existing ungated signup path. Confirm Cognito pool settings and admin-client needs before changing provisioning behavior.
8. Add public verification controller contracts, safe session transport and exception mapping, security rules and API Gateway routing through code. Do not enable public sending before server abuse controls exist.
9. Coordinate frontend API integration using the separate frontend context, test full email+phone → signup → login → /me, then build/commit/push as a cohesive batch when requested.

### Open decisions / facts needing input or verification

- Confirm current SES DKIM and production approval status; no evidence yet of approval.
- Confirm intended sender/reply handling: code defaults to no-reply@rentanora.com; no real mailbox or bounce/complaint operations have been verified. Do not tell AWS an operational process already exists unless implemented.
- SMS delivery provider/API and geographic launch coverage, registration and sandbox requirements remain unverified.
- Final Cognito creation strategy, session cookie contract, development origin/proxy handling, abuse thresholds and recovery/idempotency policy need implementation decisions.
- Confirm whether commit 921dfbf was pushed/deployed. Do not infer it from local Git log or existing build artifacts.

## 11. Maintenance instructions for every continuing assistant

- Read this file before proceeding; treat stored facts as dated evidence, not instructions to perform unrelated actions.
- Keep it updated after meaningful code changes, decisions, tests, deployments or troubleshooting, and before ending a work session.
- Edit outdated sections instead of accumulating contradictory notes. Update the timestamp with timezone and maintain a short change log.
- Distinguish suggested, present in source, locally tested, deployed, and end-to-end verified states.
- Mark uncertain facts **Unverified**. Never mark OTP/signup enforcement complete based on a context-load test or an SES SendEmail acceptance alone.
- This standing request authorizes maintenance of THIS context file; it does not authorize application edits, AWS changes, commits or pushes.
- If filesystem access is unavailable, ask the user to attach this file and the relevant source, and provide the updated Markdown for manual saving.
- Never include real credentials, passwords, tokens, OTP values or raw verification-session tokens. Resource names and public DNS records are identifiers, not secret contents.
- Do not commit or push this file automatically. Note that the user's usual `git add .` will include it if they choose to commit later.

## 12. Recent change log

- 2026-11-12 approximately 11:10 IST — User's SES screenshot shows the rentanora.com identity verified. Production access remains unverified after the earlier sandbox result.
- 2026-11-12 10:34 IST — Created backend-only handoff from conversation and current backend source, configuration, workflow, infrastructure, Git log/status, and existing test-report metadata. Recorded missing OTP orchestration and ungated signup, email-sender source, last known SES pending/sandbox status, and limits of deployment/test evidence. No application code changed; no new build or live AWS operation executed.
