## API overview
The dineoncampus API operates through GET requests with UUID and optional date parameters that return JSON files.

## Requesting location UUIDS
The UUID's of all the MTU dining halls and other food locations are returned in JSON format from this API request:

```https://api.dineoncampus.com/v1/locations/all_locations?platform=0&site_id=64872d0f351d53058416c3d5&for_menus=true&with_address=false&with_buildings=true```

A response is formatted something like this:
<details>
<summary>JSON File snippit</summary>
<pre><code>{
  "buildings": [
    {
      "id": "64b9974bc625af064571299c",
      "name": "Memorial Union Building",
      "show_menus": true,
      "type": "building",
      "allergen_filter": false,
      "locations": [
        {
          "id": "64a6ec07e45d4306a38d2ea6",
          "name": "Brkfst & Co",
          "show_menus": true,
          "type": "webtrition",
          "allergen_filter": false,
          "building_id": "64b9974bc625af064571299c",
          "active": true,
          "short_description": null,
          "custom_payment_types_used": [],
          "sort_order": 5,
          "is_delivery": false,
          "is_delivery_only": false,
          [...]
        }],
      "active": true
    }
    [...]
  ]
  "status": "success",
  "request_time": 1.597166077,
  "records": 8
}</code></pre>
</details>


The UUID's for the dining halls don't seem to change, so we should not need to run this request very often.

## Requesting Period UUID's
The period UUID's do seem to change semi-regularly, (at least once a school year). The easiest way to find the current period UUID's is to send a request to Wadsword for a weekday when it's open and ommit the period tag. This will cause it to return the default of breakfast, and the period data at the bottom of the query.

```https://api.dineoncampus.com/v1/location/64b9990ec625af0685fb939d/periods/?platform=0&date=2024-10-16```
returns
<details>
<summary>Example Request Without Period snippit</summary>
<pre><code>
{
  "status": "success",
  "request_time": 6.117609326,
  "records": 0,
  "allergen_filter": false,
  "menu": {
    "id": 1,
    "date": "2024-10-16",
    "name": null,
    "from_date": null,
    "to_date": null,
    "periods": {
      "name": "Breakfast",
      "id": "66c25f78351d5300dd7d1807",
      "sort_order": 0,
      "categories": [
        { [...]
      }]
    }
  },
  "periods": [
    {
      "id": "66c25f78351d5300dd7d1807",
      "sort_order": 0,
      "name": "Breakfast"
    },
    {
      "id": "66c25f78351d5300dd7d17fd",
      "sort_order": 1,
      "name": "Lunch"
    },
    {
      "id": "66c25f78351d5300dd7d1804",
      "sort_order": 2,
      "name": "Dinner"
    },
    {
      "id": "66cf452dc625af06298b134c",
      "sort_order": 3,
      "name": "Everyday"
    }
  ],
  "closed": false
}
</code></pre>
</details>

Note that any request with a location and date specified will include the periods in the returned query. For example:

```https://api.dineoncampus.com/v1/location/64b9990ec625af0685fb939d/periods/66c25f78351d5300dd7d17fd?platform=0&date=2024-10-16```
returns
<details>
<summary>Example Request File snippit</summary>
<pre><code>{
  "status": "success",
  "request_time": 0.353593837,
  "records": 0,
  "allergen_filter": false,
  "menu": { [...]
  },
  "periods": [
    {
      "id": "66c25f78351d5300dd7d1807",
      "sort_order": 0,
      "name": "Breakfast"
    },
    {
      "id": "66c25f78351d5300dd7d17fd",
      "sort_order": 1,
      "name": "Lunch"
    },
    {
      "id": "66c25f78351d5300dd7d1804",
      "sort_order": 2,
      "name": "Dinner"
    },
    {
      "id": "66cf452dc625af06298b134c",
      "sort_order": 3,
      "name": "Everyday"
    }
  ],
  "closed": false
}</code></pre></details>
  
## Requesting Meals


