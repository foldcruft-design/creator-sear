package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.repository.AuthRepository
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.GlassTextMuted
import com.example.ui.theme.GlassTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthDialog(
    authRepository: AuthRepository,
    strings: AppStrings = AppStrings(AppLanguage.RU),
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isSignUp by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var enableVerifiedBadge by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessCelebration by remember { mutableStateOf(false) }

    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var selectedAvatarUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val savedUri = authRepository.saveAvatarFromUri(context, uri)
                if (savedUri != null) {
                    selectedAvatarUri = savedUri
                }
            }
        }
    }

    val isUsernameValid = username.trim().length >= 2
    val isEmailValid = email.trim().contains("@") && email.trim().length >= 5
    val passwordStrength = when {
        password.length >= 8 && password.any { it.isDigit() } -> 3
        password.length >= 4 -> 2
        password.isNotEmpty() -> 1
        else -> 0
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 20.dp)
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xF215181F), Color(0xF8090B0E))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x68FFFFFF), Color(0x18FFFFFF))),
                    RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            if (showSuccessCelebration) {
                val scale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "celebration_scale"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp)
                        .scale(scale)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x45FFFFFF), Color(0x1CFFFFFF))
                                    )
                                )
                                .border(2.dp, Color(0x90FFFFFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!selectedAvatarUri.isNullOrEmpty()) {
                                AsyncImage(
                                    model = selectedAvatarUri,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Text(
                                    text = username.take(1).uppercase().ifEmpty { "C" },
                                    color = Color.White,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (enableVerifiedBadge) {
                            VerifiedBadge(
                                size = 24.dp,
                                iconSize = 14.dp,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (enableVerifiedBadge) {
                            VerifiedBadge(size = 18.dp, iconSize = 11.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = username.ifBlank { "Creator" },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = strings.regSuccessTitle,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = strings.regSuccessDesc,
                        color = GlassTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 580.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = if (isSignUp) strings.signUpHeader else strings.signInHeader,
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = strings.authSub,
                        color = GlassTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Uniform Segmented Tab Switcher (Создать аккаунт / Войти) — Identical Size & Responsive Click
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x12FFFFFF))
                            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSignUp) {
                                        Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x22FFFFFF)))
                                    } else {
                                        Brush.verticalGradient(listOf(Color(0x06FFFFFF), Color(0x03FFFFFF)))
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSignUp) Color(0x80FFFFFF) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    isSignUp = true
                                    errorMessage = null
                                }
                                .padding(horizontal = 8.dp)
                                .testTag("auth_tab_signup"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.tabSignUp,
                                color = if (isSignUp) Color.White else GlassTextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (!isSignUp) {
                                        Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x22FFFFFF)))
                                    } else {
                                        Brush.verticalGradient(listOf(Color(0x06FFFFFF), Color(0x03FFFFFF)))
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (!isSignUp) Color(0x80FFFFFF) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    isSignUp = false
                                    errorMessage = null
                                }
                                .padding(horizontal = 8.dp)
                                .testTag("auth_tab_signin"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.tabSignIn,
                                color = if (!isSignUp) Color.White else GlassTextMuted,
                                fontSize = 12.sp,
                                fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // In Sign Up: Clickable Verified Creator Checkbox + Avatar Picker Row (Pure Frosted Glass)
                    if (isSignUp) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x12FFFFFF))
                                .border(
                                    1.dp,
                                    if (enableVerifiedBadge) Color(0x55FFFFFF) else Color(0x22FFFFFF),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { enableVerifiedBadge = !enableVerifiedBadge }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("verified_author_checkbox_row")
                        ) {
                            // Frosted Glass Avatar Circle (Tap to pick photo)
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0x38FFFFFF), Color(0x16FFFFFF))
                                        )
                                    )
                                    .border(1.dp, Color(0x65FFFFFF), CircleShape)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (!selectedAvatarUri.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = selectedAvatarUri,
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (username.isNotBlank()) username.take(1).uppercase() else "C",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = Icons.Default.AddAPhoto,
                                            contentDescription = null,
                                            tint = GlassTextSecondary,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.verifiedAccountBadge,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (strings.isRu) "Нажмите на круг для фото профиля" else "Tap circle to upload avatar photo",
                                    color = GlassTextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Clean Interactive Glass Checkbox
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (enableVerifiedBadge) Color(0xE6FFFFFF) else Color(0x16FFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        if (enableVerifiedBadge) Color.White else Color(0x45FFFFFF),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { enableVerifiedBadge = !enableVerifiedBadge },
                                contentAlignment = Alignment.Center
                            ) {
                                if (enableVerifiedBadge) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Checked",
                                        tint = Color(0xFF0A0C10),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Username Field
                        GlassTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                errorMessage = null
                            },
                            placeholder = strings.placeholderUsername,
                            leadingIcon = Icons.Default.Person,
                            trailingIcon = {
                                if (isUsernameValid) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Valid",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Email Field
                    GlassTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        placeholder = strings.placeholderEmail,
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        trailingIcon = {
                            if (isEmailValid) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Valid",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Field with Eye Toggle
                    GlassTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        placeholder = strings.placeholderPassword,
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = GlassTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    )

                    // Monochrome Glass Password Strength Indicator
                    if (isSignUp && password.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val strengthText = when (passwordStrength) {
                                0, 1 -> strings.passwordWeak
                                2 -> strings.passwordMedium
                                else -> strings.passwordStrong
                            }

                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (i in 1..3) {
                                    val active = i <= passwordStrength
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (active) Color.White else Color(0x22FFFFFF))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strengthText,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Error Message Banner
                    AnimatedVisibility(visible = errorMessage != null) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x28FFFFFF))
                                    .border(1.dp, Color(0x60FFFFFF), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = errorMessage.orEmpty(),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prominent Finish / Submit Registration Button right below Password
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        GlassButton(
                            text = if (isSignUp) strings.submitSignUp else strings.submitSignIn,
                            icon = Icons.Filled.Check,
                            isPrimary = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_submit_button"),
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    val result = if (isSignUp) {
                                        authRepository.signUp(
                                            username = username,
                                            email = email,
                                            password = password,
                                            avatarUri = selectedAvatarUri,
                                            avatarIndex = selectedPresetIndex
                                        )
                                    } else {
                                        authRepository.signIn(email, password)
                                    }
                                    isLoading = false
                                    if (result.isSuccess) {
                                        showSuccessCelebration = true
                                        delay(1000)
                                        onSuccess()
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "Ошибка авторизации"
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                scope.launch {
                                    authRepository.continueAsGuest()
                                    onSuccess()
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = strings.continueGuest,
                            color = GlassTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
