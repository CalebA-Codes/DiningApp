**README.md**

# DiningApp API Usage Guide

This README provides detailed instructions and examples on how to test the DiningApp API using PowerShell's `Invoke-WebRequest` command. It includes:

* Creating users, restaurants, and admins.
* Viewing all users, restaurants, and admins.
* Creating dining reviews.
* Templates for dining reviews.

---

## 1. Creating Users

**Create User 1:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"alice","city":"New York","state":"NY","zipCode":"10001","peanutAllergy":true,"eggAllergy":false,"dairyAllergy":true}'
```

**Create User 2:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"bob","city":"Los Angeles","state":"CA","zipCode":"90001","peanutAllergy":false,"eggAllergy":true,"dairyAllergy":false}'
```

**Create User 3:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"charlie","city":"Chicago","state":"IL","zipCode":"60601","peanutAllergy":true,"eggAllergy":true,"dairyAllergy":false}'
```

---

## 2. Creating Restaurants

**Create Restaurant 1:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"Pizza Planet","location":"New York","zipCode":"10001"}'
```

**Create Restaurant 2:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"Burger Barn","location":"Los Angeles","zipCode":"90001"}'
```

**Create Restaurant 3:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"Sushi World","location":"Chicago","zipCode":"60601"}'
```

---

## 3. Creating Admins

**Create Admin 1:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"admin1"}'
```

**Create Admin 2:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"admin2"}'
```

---

## 4. Dining Review Template

Use the following JSON template for posting a dining review. Leave the `restaurantId` blank until you retrieve it from the `getAllRestaurants` endpoint:

```json
{
    "name": "alice",
    "restaurantId": ,
    "peanutScore": 4.5,
    "eggScore": null,
    "dairyScore": null,
    "review": "Great place for peanut-free dining!",
    "reviewStatus": null
}
```



* ** Additional Dining Reviews

* **Dining Review for bob at Restaurant 2:**


Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/reviews" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"bob","restaurantId":2,"peanutScore":4.0,"eggScore":3.5,"dairyScore":null,"review":"Decent place overall!","reviewStatus":null}'
```

* **Dining Review for charlie at Restaurant 3:**


Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/reviews" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"charlie","restaurantId":3,"peanutScore":5.0,"eggScore":4.5,"dairyScore":null,"review":"Amazing peanut-free options!","reviewStatus":null}'
```

---

## 5. Getting All Restaurants

**Retrieve all restaurants:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants" -Method GET
```

Use the `id` field from the JSON response to fill in the `restaurantId` field in the dining review.

---

## 6. Example Requests Per API Method

### Users

* **GET All Users:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users" -Method GET
```

* **GET User Profile:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users/alice" -Method GET
```

* **PUT Update User Profile:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users/alice" -Method PUT -Headers @{"Content-Type"="application/json"} -Body '{"city":"Brooklyn","state":"NY","zipCode":"11201","peanutAllergy":true,"eggAllergy":false,"dairyAllergy":true}'
```

* **DELETE User:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/users/alice" -Method DELETE
```

### Admins

* **GET All Admins:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins" -Method GET
```

* **DELETE Admin:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins/1" -Method DELETE
```

### Dining Reviews

* **POST Dining Review:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/reviews" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"name":"alice","restaurantId":1,"peanutScore":4.5,"eggScore":null,"dairyScore":null,"review":"Delicious food!","reviewStatus":null}'
```

* **GET All Dining Reviews:**

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/reviews" -Method GET
```

### Approved Reviews (for a specific restaurant ID)

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants/1/approved-reviews" -Method GET
```

### Admin Updates Review Status

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins/1/reviews/1?admitReview=true" -Method PUT
```

## Additional Admin and Review Endpoints

### Get Pending Reviews

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins/1/pending-reviews" -Method GET
```

### Update Review Status

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/admins/1/reviews/1?admitReview=true" -Method PUT
```

### Get Approved Reviews for a Restaurant

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants/2/approved-reviews" -Method GET
```

### Search Restaurants by Zip Code and Allergy

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/dining/restaurants/search?zipcode=10001&allergy=peanut" -Method GET
