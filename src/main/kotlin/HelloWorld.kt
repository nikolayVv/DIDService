import io.iohk.atala.prism.api.CredentialClaim
import io.iohk.atala.prism.api.KeyGenerator
import io.iohk.atala.prism.common.PrismSdkInternal
import io.iohk.atala.prism.crypto.Sha256Digest
import io.iohk.atala.prism.identity.MasterKeyUsage
import io.iohk.atala.prism.identity.PrismDid
import io.iohk.atala.prism.identity.toProto
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@OptIn(PrismSdkInternal::class)
fun main() {
    val issuer1 = Identity (
        "Marko",
        "Turk",
        "mt1234@profesor.uni-lj.si"
    )

    val holder1 = Identity (
        "Eva",
        "Bizjak",
        "eb3345@student.uni-lj.si"
    )
    val holder2 = Identity(
        "Franc",
        "Horvat",
        "fh4444@student.uni-lj.si"
    )
    val holder3 = Identity(
        "Tomaž",
        "Kotnik",
        "tk8787@student.uni-lj.si"
    )
    //issuer1.readDID("did:prism:0ab42724802464560f5068d19e574f2887dc9f8d2632781f01e8e048441bdab3")
    //issuer1.readDID("did:prism:0ab42724802464560f5068d19e574f2887dc9f8d2632781f01e8e048441bdab3:Cj8KPRI7CgdtYXN0ZXIwEAFKLgoJc2VjcDI1NmsxEiEDrwbZz5kgptfuIq4cVp_G1E-bpBJyztvt6-xpfYKiyqk")
    issuer1.publishDID()
    //holder1.publishDID()
    //holder2.publishDID()
    //holder3.publishDID()
    val holders = arrayOf(
        holder1,
        holder2,
        holder3
    )
    println(issuer1.readDID(issuer1.getDID())!!.didDataModel)
    for (holder in holders) {
        println(issuer1.readDID(holder.getUnpublishedDID().toString())!!.didDataModel)
    }
    val claims = mutableListOf<CredentialClaim>()
    for (holder in holders) {
        val holderLongDid = holder.getUnpublishedDID()

        val holderDidCanonical = holderLongDid.asCanonical().did
        val holderDidLongForm = holderLongDid.did

        println("${holder.getFullName()} canonical: $holderDidCanonical")
        println("${holder.getFullName()} long form: $holderDidLongForm")
        println()

        val credentialClaim = CredentialClaim(
            subjectDid = holderLongDid,
            content = JsonObject(mapOf(
                Pair("name", JsonPrimitive(holder.getFullName())),
                Pair("Web Programming", JsonPrimitive("Passed exam")),
                Pair("year", JsonPrimitive(2022)))
            )
        )

        claims.add(credentialClaim)
    }
    val batch = Batch(
        "Web programming",
        claims,
        "",
        mutableListOf()
    )
    issuer1.issueCredentials(batch)
    for (holder in holders) {
        println(issuer1.readDID(holder.getDID()))
    }
}
