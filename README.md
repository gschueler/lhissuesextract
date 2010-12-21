Generates Markdown formatted text from Lighthouse Issues
----------------------

*author*: Greg Schueler, <greg.schueler@gmail.com>
*date*: 12/16/2010

This is a simple groovy script that grabs the list of issues for a project and milestone that you specify, and prints it out in Markdown format.

Usage:

    groovy lhextract.groovy <url> [project [milestone]] [keyword=value ...]

Final arguments of the form `keyword=value` are used as additional ticket query parameters.  See the [Lighthouse Search Documentation](http://help.lighthouseapp.com/kb/getting-started/how-do-i-search-for-tickets) for info on types of searches.

Examples
-----

    groovy lhextract.groovy http://rundeck.lighthouseapp.com 'Rundeck Development' 'Rundeck 1.0' state=resolved
