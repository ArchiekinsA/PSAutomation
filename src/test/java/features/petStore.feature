Feature: Pet Store

  Scenario: New Pet Created Successfully
    Given a request to create a pet with following details is sent
      | name | photoUrls | status    | categoryId | categoryName | tagId | tagName |
      | Arch | test.com  | available | 1          | testCategory | 1     | testTag |
    Then response body with pet details "name" as "Arch" should be returned
    Then response body with pet details "status" as "available" should be returned
    Then response body with pet details photoUrls containing "test.com" should be returned
    Then response body with pet details category name as "testCategory" should be returned
    Then response body with pet details category id as "1" should be returned
    Then response body with pet details tag name as "testTag" should be returned
    Then response body with pet details tag id as "1" should be returned


  Scenario: Existing Pet Updated Successfully
    Given a request to create a pet with following details is sent
      | name | photoUrls | status    | categoryId | categoryName | tagId | tagName |
      | Arch | test.com  | available | 1          | testCategory | 1     | testTag |
    When pet details "name" is updated to "PetNameTwo"
    Then response body with pet details "name" as "PetNameTwo" should be returned

  Scenario: Existing Pet Can be deleted Successfully
    Given a request to create a pet with following details is sent
      | name | photoUrls | status    | categoryId | categoryName | tagId | tagName |
      | Arch | test.com  | available | 1          | testCategory | 1     | testTag |
    When created Pet is deleted
    Then message with "pet not found" is displayed during retrieval

