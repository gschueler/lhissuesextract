import java.net.URL;
import groovy.text.SimpleTemplateEngine
import groovy.text.GStringTemplateEngine
import java.util.*


/**
 * Uses Lighthouse API to get a list of closed issues given a project and milestone,
 * and prints the result in Markdown format.
 * LH API key is expected to be set in a file "lighthouse-api.properties" key of "lighthouse.api.key"
 */

if(!args || args.length<1){
    println "Usage: URL [project [milestone]]"
    System.exit(1)
}
File propfile = new File("lighthouse-api.properties")
if(!propfile.exists()){
    println "lighthouse-api.properties does not exist: "+propfile.getAbsolutePath()
    System.exit(1)
}
Properties props = new Properties()

props.load(new java.io.FileInputStream(propfile))

if(!props.containsKey("lighthouse.api.key")){
    println "lighthouse.api.key property not found: "+propfile.getAbsolutePath()
    System.exit(1)
}

String key=props.getProperty("lighthouse.api.key")

def lhurl=args[0]
def url = new URL(lhurl);

def text=url.text


if(args.length<2){
    printProjects(url)
    System.exit(0)
}
String project= args[1]

String projectId = loadProjectId(url,project)

if(!projectId){
    println "project not found: "+project
    System.exit(1)
}
//println "Project: "+projectId
if(args.length<3){
//print milestones
    printMilestones(url,projectId)
    System.exit(0)
}

String milestone=args[2]
String msid=loadMilestoneId(url,projectId,milestone)

if(!projectId){
    println "Milestone not found: "+milestone
    System.exit(1)
}

List params=new ArrayList()
params.add("milestone="+milestone)
if(args.length>3){
    params.addAll(args[3..args.length-1])
}

//load tickets

printTickets(url,projectId,params)

public printTickets(url,projectId, params){
    def xml = parseUrl(new URL(url,"/projects/"+projectId+"/tickets.xml"+makeQuery(params)))
    println "Tickets: "
    
    xml.ticket.each{
        println "* ["+it.title+"]("+url+"/projects/"+projectId+"/tickets/"+it.id+")"
    }
}
public String makeQuery(params){
    return "?q="+URLEncoder.encode(params.collect{it.split("=",2).join(":'")+"'"}.join(" "))
    
    //"?"+params.collect{it.split("=",2).collect{URLEncoder.encode(it)}.join("=")}.join("&")
}
public printMilestones(url,projectId){
    def ms = loadMilestones(url,projectId)
    println "Milestones:"
    ms.values().sort{a,b->a.title<=>b.title}.each{
        println "* "+it.title+" ("+it.id+") <"+it.permalink+">"
    }
}

public printProjects(url){
    def list = listProjects(url)
    println "Projects:"
    int i=1;
    list.each{
        println i+". "+it.name +" ("+it.id+")"
        i++
    }
}

public Map loadMilestones(url,projectId){
    def xml = parseUrl(new URL(url,"/projects/"+projectId+"/milestones.xml"))
    def ms = [:]
    xml.milestone.each{
        ms[it.id.text()]=[id:it.id.text(),title:it.title.text(),permalink:it.permalink.text()]
    }
    return ms
}
public String loadMilestoneId(url,projectId,milestone){
    if(milestone=~/^\d+$/){
        return milestone
    }
    def ms=loadMilestones(url,projectId);
    def found=ms.find{it.value.title==milestone}?.value
    return found?.id
}
public String loadProjectId(url,String project){
    if(project=~/^\d+$/){
        return project
    }
    def projects=loadProjects(url);
    def proj=projects[project]
    return proj?.id
}
public Map<String,Map<String,String>> loadProjects(URL url){
    def xml = parseUrl(new URL(url,"/projects.xml"))
    
    Map<String,Map<String,String>> projects = new HashMap<String,Map<String,String>>()
    xml.project.each{
        HashMap<String,String> data = new HashMap<String,String>()
        data.put("name",it.name.text())
        data.put("id",it.id.text())
        projects.put(it.name.text(),data);
    }
    
    return projects
}

public List<Map<String,String>> listProjects(URL url){
    def xml = parseUrl(new URL(url,"/projects.xml"))
    
    List<Map<String,String>> list = new ArrayList<Map<String,String>>()
    xml.project.each{
        HashMap<String,String> data = new HashMap<String,String>()
        data.put("name",it.name.text())
        data.put("id",it.id.text())
        list.add(data);
    }
    
    return list
}
def parseUrl(URL url){
    //println "! parseUrl: "+url
    return new XmlSlurper().parseText(url.text)
}
public String loadIssues(String milestone){
}