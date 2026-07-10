package com.ju.passgen.crypto

/**
 * Lista de 440 palabras para modo Passphrase.
 * Puerto exacto del WORD_LIST del proyecto web.
 */
object WordList {

    val WORDS: List<String> = listOf(
        "able","about","above","acid","aged","agile","alert","alpha","amber","ample",
        "angel","angle","ankle","anvil","apple","arch","arena","armor","array","arrow",
        "aspen","atlas","audio","avail","avid","axiom","azure","badge","baker","basic",
        "batch","beach","bison","black","blade","blank","blast","blaze","blend","block",
        "bloom","blown","blunt","board","bold","bolt","bonus","boost","born","brace",
        "brave","bread","brick","brief","bring","broad","brook","brush","build","bunch",
        "burst","cabin","cable","caddy","cairn","calm","carat","carve","cedar","chain",
        "chalk","chart","chase","check","chess","civic","civil","claim","clasp","class",
        "clean","clear","clerk","click","cliff","clock","clone","cloth","cloud","clove",
        "clue","coast","cobalt","codec","comet","coral","cover","craft","crane","crisp",
        "cross","crown","crush","cubic","curve","cyber","daily","dance","datum","dawn",
        "debug","delta","depot","depth","derby","drift","drink","drive","drone","dunes",
        "dusky","eagle","early","earth","edit","eight","elite","ember","empty","epoch",
        "equip","event","evoke","exact","excel","exist","extra","fable","facet","fairy",
        "faith","false","fence","fetch","field","fifth","fifty","fight","final","first",
        "fixed","flare","flask","fleet","flint","float","flood","floor","flora","flour",
        "fluid","focus","forge","found","fresh","frost","fulcrum","gauge","giant","given",
        "glade","gland","glyph","graft","grain","grand","grant","graph","grasp","grass",
        "grove","guard","guide","guild","guise","habit","hardy","haven","heart","heavy",
        "hedge","helix","hertz","hinge","honor","humid","hyper","ideal","image","index",
        "indie","inert","input","intel","inter","intro","ivory","jaguar","jewel","joker",
        "joint","judge","jumbo","juror","karma","kayak","kinetic","knife","knot","lance",
        "large","laser","latch","later","layer","learn","ledge","level","light","limit",
        "linear","link","logic","lunar","magic","major","maple","march","match","matrix",
        "merit","metal","might","minor","minus","mirth","model","modem","month","moral",
        "morse","mount","mutex","mystic","naval","nexus","noble","north","novel","nylon",
        "oblong","ocean","offset","optic","orbit","order","outer","oxide","panda","parse",
        "patch","pause","peace","phase","phone","pilot","pixel","pivot","plain","plane",
        "plant","plaza","point","polar","power","print","prism","probe","proof","proto",
        "pulse","punch","quartz","queen","query","queue","quick","quiet","quota","radar",
        "radio","rapid","ratio","reach","realm","relay","relic","remix","ridge","river",
        "robot","rocky","rover","royal","scale","scene","scope","scout","serum","shade",
        "shaft","sharp","shelf","shift","shine","sigma","sigil","siren","sixth","sized",
        "skill","slate","sleek","slice","slide","slope","smart","solar","solid","sonic",
        "south","spark","spawn","speed","spend","spire","spirit","split","squad","stack",
        "staff","stage","stale","stark","start","state","steel","steep","stern","still",
        "stock","stone","storm","story","stout","strap","strip","study","style","suite",
        "super","surge","swift","synth","table","tally","teach","terra","theme","third",
        "thorn","tiger","tight","timer","title","token","torch","total","tower","track",
        "trade","trail","train","trend","trial","triad","tribe","trick","tried","trove",
        "trunk","trust","truth","turbo","twist","typed","ultra","union","unity","until",
        "upper","uptime","urban","usage","valid","valor","vault","vector","venus","vital",
        "vivid","vortex","water","wedge","while","white","whole","wield","world","worth",
        "write","xenon","xray","yacht","yield","young","zebra","zero","zonal","zone"
    )

    val size: Int get() = WORDS.size

    fun random(): String = WORDS[CryptoRandom.nextInt(WORDS.size)]
}
