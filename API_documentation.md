## API overview
The dineoncampus API operates through GET requests with UUID and optional date parameters that return JSON files.

## Requesting location UUIDS
The UUID's of all the MTU dining halls and other food locations are returned in JSON format from this API request:
`https://api.dineoncampus.com/v1/locations/all_locations?platform=0&site_id=64872d0f351d53058416c3d5&for_menus=true&with_address=false&with_buildings=true`
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


## Requesting Period UUID's


