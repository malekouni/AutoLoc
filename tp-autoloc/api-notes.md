# Atelier 5 — Notes sur l'API REST (AutoLoc)

> Les sections C et D contiennent des exemples tirés de l'énoncé : remplacez-les par ce que vous avez
> réellement observé dans Postman, Swagger UI et SonarQube for IDE.

## A. Endpoints

Le préfixe `/api` est défini une seule fois (`server.servlet.context-path=/api`) ; les contrôleurs
écrivent `@RequestMapping("/contrats")`, sans `/api`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| AgenceController | GET /api/agences | IAgenceService.findAll() | 200 | — |
| AgenceController | GET /api/agences/{id} | IAgenceService.findById(id) | 200 | 404 |
| AgenceController | POST /api/agences | IAgenceService.create(entité) | 201 | 400 |
| AgenceController | PUT /api/agences/{id} | IAgenceService.update(id, entité) | 200 | 404, 400 |
| AgenceController | DELETE /api/agences/{id} | IAgenceService.deleteById(id) | 204 | 404 |
| EmployeController | GET /api/employes | IEmployeService.findAll() | 200 | — |
| EmployeController | GET /api/employes/{id} | IEmployeService.findById(id) | 200 | 404 |
| EmployeController | POST /api/employes | IEmployeService.create(entité) | 201 | 400 |
| EmployeController | PUT /api/employes/{id} | IEmployeService.update(id, entité) | 200 | 404, 400 |
| EmployeController | DELETE /api/employes/{id} | IEmployeService.deleteById(id) | 204 | 404 |
| VehiculeController | GET /api/vehicules | IVehiculeService.findAll() | 200 | — |
| VehiculeController | GET /api/vehicules/{id} | IVehiculeService.findById(id) | 200 | 404 |
| VehiculeController | POST /api/vehicules | IVehiculeService.create(entité) | 201 | 400 |
| VehiculeController | PUT /api/vehicules/{id} | IVehiculeService.update(id, entité) | 200 | 404, 400 |
| VehiculeController | DELETE /api/vehicules/{id} | IVehiculeService.deleteById(id) | 204 | 404 |
| EquipementController | GET /api/equipements | IEquipementService.findAll() | 200 | — |
| EquipementController | GET /api/equipements/{id} | IEquipementService.findById(id) | 200 | 404 |
| EquipementController | POST /api/equipements | IEquipementService.create(entité) | 201 | 400 |
| EquipementController | PUT /api/equipements/{id} | IEquipementService.update(id, entité) | 200 | 404, 400 |
| EquipementController | DELETE /api/equipements/{id} | IEquipementService.deleteById(id) | 204 | 404 |
| ClientController | GET /api/clients | IClientService.findAll() | 200 | — |
| ClientController | GET /api/clients/{id} | IClientService.findById(id) | 200 | 404 |
| ClientController | POST /api/clients | IClientService.create(entité) | 201 | 400 |
| ClientController | PUT /api/clients/{id} | IClientService.update(id, entité) | 200 | 404, 400 |
| ClientController | DELETE /api/clients/{id} | IClientService.deleteById(id) | 204 | 404 |
| ReservationController | GET /api/reservations | IReservationService.findAll() | 200 | — |
| ReservationController | GET /api/reservations/{id} | IReservationService.findById(id) | 200 | 404 |
| ReservationController | POST /api/reservations | IReservationService.create(entité) | 201 | 400 |
| ReservationController | PUT /api/reservations/{id} | IReservationService.update(id, entité) | 200 | 404, 400 |
| ReservationController | DELETE /api/reservations/{id} | IReservationService.deleteById(id) | 204 | 404 |
| ContratController | GET /api/contrats | IContratService.findAll() | 200 | — |
| ContratController | GET /api/contrats/{id} | IContratService.findById(id) | 200 | 404 |
| ContratController | POST /api/contrats | IContratService.create(entité) | 201 | 400 |
| ContratController | PUT /api/contrats/{id} | IContratService.update(id, entité) | 200 | 404, 400 |
| ContratController | DELETE /api/contrats/{id} | IContratService.deleteById(id) | 204 | 404 |
| MaintenanceController | GET /api/maintenances | IMaintenanceService.findAll() | 200 | — |
| MaintenanceController | GET /api/maintenances/{id} | IMaintenanceService.findById(id) | 200 | 404 |
| MaintenanceController | POST /api/maintenances | IMaintenanceService.create(entité) | 201 | 400 |
| MaintenanceController | PUT /api/maintenances/{id} | IMaintenanceService.update(id, entité) | 200 | 404, 400 |
| MaintenanceController | DELETE /api/maintenances/{id} | IMaintenanceService.deleteById(id) | 204 | 404 |
| PaiementController | GET /api/paiements | IPaiementService.findAll() | 200 | — |
| PaiementController | GET /api/paiements/{id} | IPaiementService.findById(id) | 200 | 404 |
| PaiementController | POST, PUT, DELETE /api/paiements | aucun (non exposé) | — | 405 |

Les 404 viennent de `ResourceNotFoundException` et les 400 de `IllegalArgumentException`, traduites par
le `GlobalExceptionHandler` : les contrôleurs ne produisent jamais ces codes eux-mêmes. Le 405 sur
`/api/paiements` est la réponse standard de Spring MVC quand le chemin existe mais pas le verbe.

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
|---|---|---|
| `Contrat.paiements` (OneToMany, `cascade = ALL`, `orphanRemoval`) | `@JsonManagedReference("contrat-paiements")` | Composition : le paiement n'existe que dans son contrat. La liste est sérialisée avec le contrat, et lue dans le JSON de création (cascade). |
| `Paiement.contrat` (ManyToOne) | `@JsonBackReference("contrat-paiements")` | Côté masqué : coupe la boucle contrat → paiements → contrat. À la lecture du JSON, Jackson renseigne seul `contrat` sur chaque paiement. |
| Listes inverses (`mappedBy`) : `Agence.employes`, `Agence.vehicules`, `Vehicule.reservations`, `Vehicule.maintenances`, `Client.reservations`, `Equipement.vehicules`, côté inverse de Reservation ↔ Contrat | `@JsonIgnore` | Les enfants sont des entités indépendantes, créées seules avec `"agence": {"idAgence": 1}`. La liste n'a pas à figurer dans le JSON du parent. |
| Côté propriétaire (`Employe.agence`, `Vehicule.agence`, `Reservation.client`, `Reservation.vehicule`, `Maintenance.vehicule`) | aucune | Doit rester lisible et inscriptible : c'est lui qui porte la clé étrangère. |

**`@JsonBackReference` ou `@JsonIgnore` ?** Les deux masquent la propriété à l'écriture du JSON. La
différence se voit à la lecture : `@JsonBackReference` fait rétablir le lien parent par Jackson (le
paiement reçu dans un contrat récupère son contrat), alors que `@JsonIgnore` ignore la propriété dans les
deux sens. Sur `Reservation.client`, un `@JsonBackReference` ferait ignorer le client à la lecture : le
POST créerait une réservation sans client.

## C. Postman et Swagger

- **Postman** sert à tester : requêtes rejouables, assertions sur les réponses, enchaînement par variables
  d'environnement. Exemple : `GET /api/contrats/9999` doit renvoyer 404 avec un corps JSON
  (`statut`, `message`, `horodatage`), vérifié par `pm.response.to.have.status(404)` ; le scénario
  « Créer un contrat avec ses paiements » vérifie le 201 et la présence des deux paiements.
- **Swagger UI** sert à documenter et explorer : la description est générée par SpringDoc à partir des
  contrôleurs, donc toujours synchronisée avec le code. Exemple : `@ApiResponse(responseCode = "404")` sur
  `findById` apparaît dans le groupe « Contrats », et le bouton *Try it out* envoie la requête sans écrire
  de JSON ailleurs.
- Ils sont complémentaires : Postman prouve que l'API fonctionne, Swagger permet à un tiers de la
  comprendre et de l'essayer.

## D. Anomalies SonarQube for IDE

> À compléter avec vos propres relevés : lancez *Analyze with SonarQube for IDE* sur `web.controller`.
> Les deux lignes ci-dessous sont des exemples à confirmer ou à remplacer par ce que votre panneau affiche.

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Entité JPA utilisée comme paramètre `@RequestBody` d'un contrôleur (exemple, règle S4684 à confirmer) | Exposer une entité persistante couple l'API à la base et permet de renseigner n'importe quel champ. | Conservé volontairement jusqu'au chapitre 10 (DTO), comme l'indique l'énoncé. |
| Exemple à remplacer : import inutilisé, injection par attribut ou nom non conforme | Code mort ou mauvaise pratique d'injection. | Suppression de l'import, ou injection par constructeur (`@RequiredArgsConstructor`, champ `final`). |

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur ?** Le
   `DispatcherServlet`, point d'entrée unique. Il interroge le `HandlerMapping`, qui compare le verbe HTTP
   et l'URL aux annotations `@RequestMapping`, `@GetMapping`, etc. des contrôleurs, et désigne la méthode
   à appeler.
2. **Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de
   `ContratServiceImpl` ?** Le contrôleur reste mince : il ne connaît que la couche juste en dessous, par
   son interface. S'il appelait le repository, il contournerait les règles de gestion (garde sur l'id,
   règles de validation, traduction de l'absence en `ResourceNotFoundException`). S'il dépendait de
   l'implémentation, on ne pourrait plus la remplacer (test, autre implémentation) sans toucher au contrôleur.
3. **Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et
   non 500 ?** 201 signale qu'une ressource a été créée ; 204 signale un succès sans corps à renvoyer
   puisque la ressource n'existe plus. Une ressource absente est une erreur du client (il a demandé un
   identifiant qui n'existe pas) : le serveur fonctionne normalement, d'où un 404. Un 500 signale un
   dysfonctionnement inattendu du serveur.
4. **Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST
   `/api/paiements` ?** Un paiement n'existe qu'au sein de son contrat (composition, `cascade = ALL`,
   `orphanRemoval`) : il est créé et supprimé par le Contrat, qui garde la cohérence. Un POST sur
   `/api/paiements` reçoit **405 Method Not Allowed** : le chemin existe (GET), mais pas ce verbe.
5. **Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il
   `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?** Risques : couplage de
   l'API au schéma de la base, champs internes visibles, modification possible de n'importe quel champ
   depuis le JSON, boucles de sérialisation sur les associations bidirectionnelles, erreurs et requêtes
   supplémentaires avec les proxys LAZY, API difficile à faire évoluer. Les paiements forment une
   composition : ils sont créés et affichés dans le JSON de leur contrat, d'où la paire
   `@JsonManagedReference` / `@JsonBackReference`. Les véhicules sont indépendants : on les crée seuls avec
   `"agence": {"idAgence": 1}` et la liste n'a pas à figurer dans l'agence, d'où `@JsonIgnore` sur la liste
   inverse.
