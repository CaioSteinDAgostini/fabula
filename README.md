
# fabula

The purpose of this project if to create a backend that manages document entries. Something like a blog application.
And, more than anything else, illustrate the usage of some technologies, such as:

- Spring Boot MVC
- Spring Boot Data (JPA/Jakarta)
- OpenAPI documentation
- JWT (I know that there are off the shelf solutions, including solutions integrated with Spring. But I really wanted to try and make it by myself)


## The main entities/concepts

### Domain

Once it is running, the application has domains - e.g.:
- *technology*
- *sports*
- *politics*
The access to the domains can be restricted through authorization configurations.
> I did not use Spring Security for the authentication and authorization. Just because I wanted to create a simple solution by myself. However, the security solution is certainly not production worthy.

 There are no predefined domains. And one domain can have subdomains - e.g.:
 - *technology*
	 - *hardware*
	 - *software*.

Every instance of the application has a default, unamed, "root" domain.

### Document

A document is.. well, a document. Its content is supposed to be a markdown document.
Each document:
-  can be assigned a title/thumbnail image (**file**).
-  has an author (authorship, not implemented yet)
-  can have tags (also not yet implemented)
-  and is assigned to a **domain**

### User

A user is the individual that uses the software. It has:
- a name
- a creation date
- and it can have multiple **accounts**

### Account
An account has 1 **domain** (1-to-n) and it has 1 **user** (n-to-1)

## The interesting implementation parts and tools

### Embeddable  AccountId @Entity
AccountId is the Id for an **Account**.

```java
@Embeddable
public class AccountId implements Serializable{
    @Column(name = "domain")
    UUID domainId;
    @Column(name = "username")
    String username;
```

It has an UUID identifier (its public id) and an username (which is the **User** entity's id)

### The  Account @Entity
```java
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"username", "domain"}))
public class Account implements IResource {

    @JsonIgnore
    @EmbeddedId
    AccountId id;

    @ManyToOne
    @MapsId("username")
    @JoinColumn(name = "username")
    @NotNull
    User user;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDateTime;

    @ManyToOne
    @MapsId("domain")
    @JoinColumn(name = "domain")
    Domain domain;

    @ManyToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    Set<Role> roles;
```
Which, besides the EmbeddedId, also has a many-to-many relation, which **Role** (which is part of the authorization solution)

### Three types of repository (CrudRepository, JpaRepository, PagingAndSortingRepository)

Examples

#### CrudRepository

```java
@Transactional
public interface CrudDocumentRepository extends CrudRepository<Document, UUID> {

    List<Document> findByTitle(String title);

    List<Document> findByDomain(Domain domain);

    List<Document> findByDomainAndRestrictedFalse(Domain domain);
}
```

#### PagingAndSortingRepository and JpaRepository

```java
@Transactional
public interface PageDocumentRepository extends PagingAndSortingRepository<Document, UUID>, JpaRepository<Document, UUID> {

    @Query(value = "SELECT document FROM Document document WHERE document.domain = :domain")
    Page<Document> findByDomainWithPagination(Domain domain, Pageable pageable);

    @Query(value = "SELECT document FROM Document document WHERE document.domain = :domain AND not document.restricted" )
    Page<Document> findAByDomainAndRestrictedFalseWithPagination(Domain domain, Pageable pageable);
}
```

and its paged response

```json
{
  "content": [
    {
      "id": "6b7b7cee-94be-4d3d-b1fc-a6733a1972fa",
      "title": "Sunflowers!",
      "titleImage": "e668464f-405b-4d52-8971-4a59d9a68675",
      "subtitle": "Hello Document",
      "contents": "# Title\n\n## Subtitle\n\ncontent\n\ncontent2\n\n- item 1\n   - subitem\n  - subitem2\n- item2",
      "domain": {
        "id": "e38a289f-358a-473c-885b-f0d00cbd8f13",
        "name": "Fabula",
        "restricted": false,
        "root": true
      },
      "author": null,
      "creationDateTime": "2023-12-02T13:57:20.447253",
      "restricted": false,
      "creationDate": "2023-12-02T13:57:20.447253"
    },
    {
      "id": "06b7c2ae-44b7-43de-b8c7-98de79e5c224",
      "title": "Broken algorithm ",
      "titleImage": "8232a67a-c0e2-492e-9b81-ddfebd54bafd",
      "subtitle": "Hello Document",
      "contents": "# Title\n\n## Subtitle\n\ncontent\n\ncontent2",
      "domain": {
        "id": "e38a289f-358a-473c-885b-f0d00cbd8f13",
        "name": "Fabula",
        "restricted": false,
        "root": true
      },
      "author": null,
      "creationDateTime": "2023-12-02T13:57:20.451012",
      "restricted": false,
      "creationDate": "2023-12-02T13:57:20.451012"
    },
    {
      "id": "341191ec-6993-4fc9-9e7e-03df89b61e7b",
      "title": "Document4",
      "titleImage": null,
      "subtitle": "Hello Document 4",
      "contents": "# Title\n\n## Subtitle\n\ncontent\n\ncontent2",
      "domain": {
        "id": "e38a289f-358a-473c-885b-f0d00cbd8f13",
        "name": "Fabula",
        "restricted": false,
        "root": true
      },
      "author": null,
      "creationDateTime": "2023-12-02T13:57:20.454334",
      "restricted": false,
      "creationDate": "2023-12-02T13:57:20.454334"
    }
  ],
  "pageable": {
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 3,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 4,
  "totalElements": 11,
  "last": false,
  "size": 3,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 3,
  "empty": false
}
```
### The OpenAPI documentation
It is done using the package
It can be accessed on http://localhost:8080/v3/api-docs and http://localhost:8080/swagger-ui/index.html .
Is is possible to further detail the auto-generated content (but I did not do it for all the controller methods, but you can see an example bellow)

```java
 @Operation(summary = "Delete an account for the domain identified by 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @DeleteMapping("/domains/{domainId}/accounts")
    public ResponseEntity<Account> deleteAccount(@RequestHeader("Authorization") String bearer, @RequestParam String username, @PathVariable("domainId") UUID domainId) {
```
## How to make it run

There is an Starter class that bootstraps some data into the (in memory, h2, database)

```java
@Component
public class Starter implements InitializingBean {

    @Autowired
    UserAndAccountService accountService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DomainService domainService;
    @Autowired
    DomainRepository domainRepository;
    @Autowired
    DocumentService documentService;
    @Autowired
    FileService fileService;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    ImageService imageService;

    @Override
    public void afterPropertiesSet() throws Exception {

        User rootUser = accountService.createUser("root@domain.com").get();
        Domain fabula = domainService.createRootDomain(rootUser, "Fabula").get();
        Domain fabulaBlog = domainService.createDomain(rootUser, "Fabula Blog", fabula).get();
        Domain fabulaProject = domainService.createDomain(rootUser, "Fabula Project", fabula).get();

        User demoUser1 = accountService.createUser("demo1@domain.com").get();
        User demoUser2 = accountService.createUser("demo2@domain.com").get();
        User demoUser3 = accountService.createUser("demo3@domain.com").get();

        byte[] straighSkeletonImageBytes = Files.readAllBytes(Paths.get("/home/caio/Imagens/Screenshot_20230202_180425.png"));
        File straighSkeletonImage = fileService.create("straight skeleton", straighSkeletonImageBytes, "image/png", fabula).get();
        Optional<ImageThumbnail> straighSkeletonImageThumbnail = imageService.makeThumbnail(straighSkeletonImage);

        byte[] sunflowerImageBytes = Files.readAllBytes(Paths.get("/home/caio/Imagens/sunflower.JPG"));
        File sunflowerImage = fileService.create("sunflower", sunflowerImageBytes, "image/jpg", fabula).get();
        Optional<ImageThumbnail> sunflowerImageThumbnail = imageService.makeThumbnail(sunflowerImage);

        Document doc1 = documentService.create("Sunflowers!", "Hello Document", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2\n\n- item 1\n   - subitem\n  - subitem2\n- item2", false, fabula, sunflowerImage).get();
        doc1.setTitleImage(sunflowerImage);
        documentService.save(doc1);
        Document doc2 = documentService.create("Broken algorithm ", "Hello Document", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, straighSkeletonImage).get();
        doc2.setTitleImage(straighSkeletonImage);
        documentService.save(doc2);
        Document doc3 = documentService.create("Document3", "Hello Document", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabulaBlog, null).get();
//

        Account rootFabulaAccount = accountService.createOrRecoverAccount(rootUser, fabula).get();
        Account rootFabulaBlogAccount = accountService.createOrRecoverAccount(rootUser, fabulaBlog).get();
        Account demoUser1FabulaAccount = accountService.createOrRecoverAccount(demoUser1, fabula).get();
        Account demoUser2FabulaBlogAccount = accountService.createOrRecoverAccount(demoUser2, fabulaBlog).get();
        Account demoUser2FabulaProjectAccount = accountService.createOrRecoverAccount(demoUser2, fabulaProject).get();
        Account demoUser3FabulaProjectAccount = accountService.createOrRecoverAccount(demoUser3, fabulaProject).get();
//

        HttpMethod[] actions = HttpMethod.values();
        Set<Permission> permissions = new HashSet<>();
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Document.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Permission.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, User.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Account.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Domain.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, File.class, actions));
////        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Authorship.class, actions));
//
        Role fabulaAdmin = authorizationService.createRole("admin", fabula, permissions);
        fabulaAdmin.addAccount(rootFabulaAccount);
        fabulaAdmin = authorizationService.saveRole(fabulaAdmin);

        Role fabulaDocumentAll = authorizationService.createRole("fabulaDocumentAll", fabula, authorizationService.createOrRecoverPermissions(true, Document.class, actions));
        fabulaDocumentAll.addAccount(demoUser1FabulaAccount);
        fabulaDocumentAll = authorizationService.saveRole(fabulaDocumentAll);

        Role fabulaDomainAll = authorizationService.createRole("fabulaDomainAll", fabula, authorizationService.createOrRecoverPermissions(true, Domain.class, actions));
        fabulaDomainAll.addAccount(demoUser1FabulaAccount);
        fabulaDomainAll = authorizationService.saveRole(fabulaDomainAll);

        Role fabulaFileAll = authorizationService.createRole("fabulaFileAll", fabula, authorizationService.createOrRecoverPermissions(true, File.class, actions));
        fabulaDomainAll.addAccount(demoUser1FabulaAccount);
        fabulaDomainAll = authorizationService.saveRole(fabulaDomainAll);

        Role fabulaBlogDocumentAll = authorizationService.createRole("fabulaReadAll", fabulaBlog, authorizationService.createOrRecoverPermissions(true, Domain.class, actions));
        fabulaBlogDocumentAll.addAccount(demoUser2FabulaBlogAccount);
        fabulaBlogDocumentAll = authorizationService.saveRole(fabulaBlogDocumentAll);

        Role fabulaProjectAccountAll = authorizationService.createRole("fabulaReadAll", fabulaProject, authorizationService.createOrRecoverPermissions(true, Account.class, actions));
        fabulaProjectAccountAll.addAccount(demoUser2FabulaProjectAccount);
        fabulaProjectAccountAll.addAccount(demoUser3FabulaProjectAccount);
        fabulaProjectAccountAll = authorizationService.saveRole(fabulaProjectAccountAll);

    }
```
In order to start the application, just run

```bash
mvn spring-boot:run
```

## How to Dockerize it

```bash
mvn clean package
docker build --tag=fabula:latest .
docker run -p8888:8080 fabula:latest
```


