# This file contains the complete list of tests used to validate the implementation

## ClientControllerTest.java

- GET /clients
- GET /clients -> Error codes
- GET /clients -> Pagination
- GET /clients -> Pagination with errors
- GET /clients -> sorting name ASC
- GET /clients -> sorting name DESC
- GET /clients/{id}
- GET /clients/{id} -> Error codes
- PATCH /clients/{id}
- PATCH /clients/{id} -> error codes
- DELETE /clients/{id}
- DELETE /clients/{id} -> Error codes
- GET /clients/{id}/contracts/active
- GET /clients/{id}/contracts/active -> wrong UUID
- Get active contracts for a client after a given update date
- Get no active contracts for a client after a given update date
- GET /clients/{id}/contracts/after -> bad date format
- GET /clients/{id}/contracts/costsum
- GET /clients/{id}/contracts/costsum -> wrong UUID

## CompanyControllerTest.java

- POST /clients/companies
- POST /clients/companies -> Error codes
- GET /clients/companies

## ContractControllerTest.java

- GET /contracts
- GET /contracts -> Error codes
- GET /contracts -> Pagination
- GET /contracts -> Pagination with errors
- GET /contracts/{id}
- GET /contracts/{id} -> Error codes
- PATCH /contracts/{id}
- PATCH /contracts/{id} -> error codes
- DELETE /contracts/{id}
- DELETE /contracts/{id} -> Error codes

## PersonControllerTest.java

- POST /clients/persons
- POST /clients/persons -> Error codes
- GET /clients/persons

## ClientMapperTest.java

- toDto(Person)
- toDto(Company)
- toEntity(PersonDto)
- toEntity(CompanyDto)
- toDtoList(List<? extends Client>)
- toEntityList(List<? extends ClientDto>)

## ContractMapperTest.java

- toEntity(ContractDto, Client)
- toDto(Contract)
- toDtoList()

## ClientJsonTest.java

- No test since Client class is abstract

## CompanyJsonTest.java

- Deserialization -> Single Company
- Deserialization -> List
- Serialization -> Single Company
- Serialization -> List

## ContractJsonTest.java

- Deserialization -> Single Contract
- Deserialization -> List
- Serialization -> Single Contract
- Serialization -> List

## PersonJsonTest.java

- Deserialization -> Single Person
- Deserialization -> List
- Serialization -> Single person
- Serialization -> List

## ClientRepositoryTest.java

- Read -> Find All Clients
- Read -> Find By ID
- Create -> Insert New Person (Client's child)
- Create -> Insert New Company (Client's child)
- Update -> Partial update a Client
- Delete -> Soft Delete Client

## CompanyRepositoryTest.java

- Read -> Find All Company
- Read -> Exists by company identifier
- (Create -> Tested in ClientRepositoryTest)

## ContractRepositoryTest.java

- Read -> Find All Contracts
- Read -> Find By ID
- Create -> Insert New Contract
- Create -> Insert New Contract without date
- Update -> Modify existing contract cost
- Delete -> Soft Delete Contract
- On database fetch (postLoad) -> set previousCostAmount
- Find only the active contracts for one client
- Find zero active contract if endDate is before the given date
- Find active contracts for a client after a given update date
- Find zero active contract if updatedAfter is in the future
- Sum of all active contracts for a client

## PersonRepositoryTest.java

- Read -> Find All Persons
- (Create -> Tested in ClientRepositoryTest)

## ClientServiceTest.java

- Read -> Find All Clients
- Read -> Find By ID
- Read -> Pagination
- Update -> Partial update
- Delete -> Soft Delete Client
- Delete -> Already Deleted
- Read, Patch, Delete -> ClientNotFoundException
- Validate fields -> ClientInvalidDataException (unique email and phone)

## CompanyServiceTest.java

- Read -> Find All Companies
- Create - Create a company
- Validate fields -> ClientInvalidDataException (unique company_identifier)

## ContractServiceTest.java

- Read -> Find All Contracts
- Read -> Find By ID
- Read -> Pagination
- Create -> Create a Contract
- Update -> Partial update
- Delete -> Soft Delete Contract
- Read, Patch, Delete -> ContractNotFoundException
- Read -> Only the active contracts for one client
- Read -> Only the active contracts for a client after a given update date
- Read -> zero active contract if updatedAfter is in the future
- Sum of all active contracts for a client

## PersonServiceTest.java

- Read -> Find All Persons
- Create - Create a person
