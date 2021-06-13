package io.github.blackmo18.shopify.model.payload.incoming

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
    val access_token: String,
    val scope: String,
    val expires_in: Long? = null,
    val associated_user_scope: String? = null,
    val associated_user: AssociatedUser? = null,
    var shop: String = ""
)

@Serializable
data class AssociatedUser (
    val id: Long,
    val first_name: String,
    val last_name: String,
    val email: String,
    val email_verified: Boolean,
    val account_owner: Boolean,
    val locale: String,
    val collaborator: Boolean
)

