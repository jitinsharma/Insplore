# Insplore

The app allows user to search for various locations.
- App shows user's current location on a map.
- Nearby allows user to search for nearby locations from Google Places API.
- Top Destination search allows user to search top flight destinations from a point of origin.
- Inspire Me allows user to do an inspiration search based on his current location.
- Places of Interest displays various places of interest for a given city.
- User can favorite a place of interest which is stored locally.

## Configuration
- Create an account on http://www.sandbox.amadeus.com and generate an API key by creating an app
- Put the api key in strings.xml (<string name="ama_sandbox"></string>)
- Create a google maps api key.
- Put the api key in (src/debug/res/values/google_maps_api.xml)