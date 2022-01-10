# RecipeApp
This is an app which implements an algorithm to "scan" a web recipe and extract title, description, ingredients, instructions, image, yield and cook time. 

:heavy_exclamation_mark: At this time the algorithm only works for swedish recipes.

Simplified overview of the "scanning" process:
* First search the HTML for a script tag with type "application/ld+json" or "application/javascript" which in most cases has all this data in a JSON blob. 
* If this script tag is found then you find the JSON where the field "@type" has value "Recipe" and then it's possible to extract the data.
* If this script tag is not found then some data can be found in og:XXX meta tags. However ingredients and instructions usually require that you traverse the HTML and find this data by other means:
  * The HTML can be visualized in a tree structure and traversed in a depth first search fashion. 
  * All tags are then scored based on a few criteria to find the best matches for ingredients/intructions (see code for exact criteria).
  * The two top scoring nodes for ingredients and instructions respectively are singled out and the LCA (lowest common ancestor) is determined and then all child nodes are considered ingredients/instructions.
 
 Note that this process will fail sometimes however some testing has shown that it's quite rare. Furthermore the algorithm sometimes gives back extra data which is intention as it otherwise might miss some data which is worse - the user can always edit the recipe and delete any extra data.
