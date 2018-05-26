package eu.selfhost.riegel.superfit.utils

import org.xmlpull.v1.XmlSerializer

fun XmlSerializer.document(encoding: String, standAlone: Boolean, init: XmlSerializer.() -> Unit): XmlSerializer {
    this.startDocument(encoding, standAlone)
    this.init()
    this.endDocument()
    this.flush()
    return this
}

fun XmlSerializer.element(namespace: String?, name: String, init: XmlSerializer.() -> Unit): XmlSerializer {
    this.startTag(namespace, name)
    this.init()
    this.endTag(namespace, name)
    return this
}

fun XmlSerializer.element(namespace: String?, name: String, content: String) {
    this.startTag(namespace, name)
    this.text(content)
    this.endTag(namespace, name)
}

fun XmlSerializer.cdata(namespace: String?, name: String, cdata: String) {
    this.startTag(namespace, name)
    this.cdsect(cdata)
    this.endTag(namespace, name)
}


