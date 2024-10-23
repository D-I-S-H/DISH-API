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
}</code></pre>
</details>

## Requesting Meals
Meal data is requested by specifying three parameters:

- Location (by UUID) - The dining hall that is serving the food a.k.a Wadsworth, McNair, or DHH. Up to date UUID's should be grabbed from the dineoncampus website.
  - Wadsworth : 64b9990ec625af0685fb939d
  - McNair : 64a6b628351d5305dde2bc08
  - DHH : 64e3da15e45d430b80c9b981
- Period (by UUID) - The meal of the day a.k.a Breakfast, Lunch, Dinner, Everyday. UUID's for each can be requested with the method provided above
- Date - provided as YYYY-MM-DD

To this URL: `https://api.dineoncampus.com/v1/location/[LOCATION]/periods/[PERIOD]?platform=0&date=[DATE]`

The result is a JSON containing menu information if the dining hall is open, or an indicator if it is closed.
 <details>
   <summary>Open Hall Example JSON</summary>
<pre><code>{
  "status": "success",
  "request_time": 0.353593837,
  "records": 0,
  "allergen_filter": false,
  "menu": {
    "id": 1,
    "date": "2024-10-16",
    "name": null,
    "from_date": null,
    "to_date": null,
    "periods": {
      "name": "Lunch",
      "id": "66c25f78351d5300dd7d17fd",
      "sort_order": 1,
      "categories": [
        {
          "id": "66c25f78351d5300dd7d17f7",
          "name": "Homestyle",
          "sort_order": 1,
          "items": [
            {
              "id": "6711c8022b7b7acd38ae1f21",
              "name": "BBQ Beef Meatballs",
              "mrn": 126075,
              "rev": null,
              "mrn_full": "126075.1",
              "desc": "Baked Meatballs with Cattleman BBQ Sauce",
              "webtrition_id": null,
              "sort_order": 1,
              "portion": "4 each",
              "qty": null,
              "ingredients": "Beef Meatballs, Barbecue Sauce",
              "nutrients": [
                {
                  "id": "6711c8d6351d5306459f6ec2",
                  "name": "Calories",
                  "value": "410",
                  "uom": "kcal",
                  "value_numeric": "410"
                },
                {
                  "id": "6711c8d6351d5306459f6ec3",
                  "name": "Protein (g)",
                  "value": "15",
                  "uom": "g",
                  "value_numeric": "15"
                }],
              "custom_allergens": null,
              "calories": "160"
            }
          ]
        }
      ]
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
}</code></pre>
</details>

<details>
  <summary>Closed Hall Example JSON</summary>
<pre><code>{
  "status": "success",
  "request_time": 0.017233632,
  "records": 0,
  "closed": true
}</code></pre>
</details>

### Structure of Categories
Menu items are grouped by "category". For MTU dining halls, these correspond to the different physical stations within the dining hall.
The list of categories can be accessed at `menu.periods.categories`, which is a list of categories. Each category has:
- `id` - UUID
- `name` - category name (what that station is called in the dining hall, "Flame", "Homestyle", etc.
- `sort_order` - unknown purpose, usually just the number 1
- `items` - list of menu items.

### Structure of Menu Items
For a given category, a list of all menu items within that category can be accessed at `menu.periods.categories[#].items`. Each item has:
- `id` - UUID, unique to both the menu item, and the day it is served (can't be used to identify menu items across days)
- `name` - name of menu item
- `mrn` - possibly a Movement Reference Number?
- `rev` - some form of revision number, most likely can be disreguarded as it's usually set to null (Might be populated if this menu item is updated after it's published?)
- `mrn_full` - full mrn number, includes an extra zero or one after a decimal point
- `desc` - description of a menu item. Not standardised, but usually contains a gracious description of a partial list of ingredients. Can be null.
- `webtrition_id` - possibly a reference to the webtrition database of recipies. Always null.
- `sort_order` - unknown purpose, usually just the number 1
- `portion` - text describing a portion quantity
- `qty` - probably quantiy, always null.
- `ingredients` - string listing all ingredients, comma separated.
- `nutrients` - list of nutrients (like on nutrition labels) Each nutrient has:
  - `id` - UUID
  - `name` - name of nutrient with units e.g. "Total Carbohydrates (g)"
  - `value` - quantity of the nutrient
  - `uom` - unit abreviation
  - `value_numeric` - quantity of the nutrient, guaranteed to be numeric?
- `filters` - list of groups that can be filtered by e.g. different allergens, vegetarian, vegan. Has the feilds:
  - `id` - UUID
  - `name` - name of filter
  - `type` - type of filter, usually "allergen" for allergens or "label" for everything else
  - `icon` - boolean for if there is an icon image file associated with this filter, usually only true for filters of type label
  - `remote_file_name` - name of icon file, null if `icon` is false
  - `sector_icon_id` - number, unknown purpose
  - `custom_icons` - list, usually empty
- `custom allergens` - probably a string for a comma separated list of allergens
- `calories` - string of numeric value of calories for the menu item (presumably at the given portion)
