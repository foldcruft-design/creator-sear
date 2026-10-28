package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    var isSignUp by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedAvatarUri by remember { mutableStateOf<String?>(null) }
    var selectedPresetIndex by remember { mutableIntStateOf(0) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessCelebration by remember { mutableStateOf(false) }

    val isEmailValid = remember(email) {
        email.contains("@") && email.contains(".") && email.length >= 5
    }
    val isUsernameValid = remember(username) { username.trim().length >= 2 }
    val passwordStrength = remember(password) {
        when {
            password.length < 4 -> 0
            password.length in 4..6 -> 1
            password.any { it.isDigit() } && password.any { it.isLetter() } && password.length >= 7 -> 3
            else -> 2
        }
    }

    // Photo picker launcher for custom avatar
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val localPath = authRepository.saveAvatarFromUri(context, uri)
                selectedAvatarUri = localPath ?: uri.toString()
            }
        }
    }

    val presetGradients = listOf(
        listOf(Color(0xFF00E5FF), Color(0xFF0072FF)), // Cyan Neon
        listOf(Color(0xFF8A2BE2), Color(0xFF4A00E0)), // Royal Purple
        listOf(Color(0xFF10B981), Color(0xFF059669)), // Emerald Mint
        listOf(Color(0xFFFF8A00), Color(0xFFE52E71))  // Sunset Coral
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xF012141A), Color(0xF808090D))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x70FFFFFF), Color(0x18FFFFFF))),
                    RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            if (showSuccessCelebration) {
                // Success celebration state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00E5FF), Color(0xFF0066FF))
                                )
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF04060A),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = strings.regSuccessTitle,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = strings.regSuccessDesc,
                        color = GlassTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x2200E5FF))
                            .border(1.dp, Color(0x7000E5FF), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        VerifiedBadge(size = 16.dp, iconSize = 10.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.sessionPersistentNotice,
                            color = Color(0xFFE0F7FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Badge
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0x35FFFFFF), Color(0x10FFFFFF))
                                )
                            )
                            .border(1.5.dp, Color(0x65FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isSignUp) strings.signUpHeader else strings.signInHeader,
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = strings.authSub,
                        color = GlassTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab Switcher (Регистрация / Вход)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x15FFFFFF))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(13.dp))
                                .background(if (isSignUp) Color(0x3AFFFFFF) else Color.Transparent)
                                .clickable {
                                    isSignUp = true
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.tabSignUp,
                                color = if (isSignUp) Color.White else GlassTextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(13.dp))
                                .background(if (!isSignUp) Color(0x3AFFFFFF) else Color.Transparent)
                                .clickable {
                                    isSignUp = false
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.tabSignIn,
                                color = if (!isSignUp) Color.White else GlassTextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // In Sign Up: Avatar Chooser + Verified Preview Banner
                    if (isSignUp) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x10FFFFFF))
                                .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(16.dp))
                                .padding(10.dp)
                        ) {
                            // Avatar Preview with pinned Verified Badge
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            presetGradients[selectedPresetIndex % presetGradients.size]
                                        )
                                    )
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
                                        modifier = Modifier.size(50.dp).clip(CircleShape)
                                    )
                                } else {
                                    Text(
                                        text = if (username.isNotBlank()) username.take(1).uppercase() else "C",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VerifiedBadge(size = 14.dp, iconSize = 9.dp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = strings.verifiedAccountBadge,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (strings.isRu) "Нажмите на круг чтобы выбрать своё фото" else "Tap circle to upload your photo",
                                    color = GlassTextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                        tint = Color(0xFF00E5FF),
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
                                    tint = Color(0xFF00E5FF),
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

                    // Password strength indicator in Sign Up mode
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
                            val barColor = when (passwordStrength) {
                                0, 1 -> Color(0xFFFFAA00)
                                2 -> Color(0xFF00D2FF)
                                else -> Color(0xFF10B981)
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
                                            .background(if (active) barColor else Color(0x20FFFFFF))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strengthText,
                                color = barColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Notice that checkmark will be saved permanently
                    if (isSignUp) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            VerifiedBadge(size = 13.dp, iconSize = 8.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.verifiedPillTip,
                                color = GlassTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
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
                                    .background(Color(0x35FF3344))
                                    .border(1.dp, Color(0x70FF3344), RoundedCornerShape(12.dp))
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

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        GlassButton(
                            text = if (isSignUp) strings.submitSignUp else strings.submitSignIn,
                            isPrimary = true,
                            modifier = Modifier.fillMaxWidth(),
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
                                        delay(1200)
                                        onSuccess()
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "Ошибка авторизации"
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    authRepository.continueAsGuest()
                                    onSuccess()
                                }
                            }
                            .padding(vertical = 6.dp)
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
