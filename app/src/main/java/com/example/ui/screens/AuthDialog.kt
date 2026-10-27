package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.AuthRepository
import com.example.domain.model.AppLanguage
import com.example.domain.model.AppStrings
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassTextField
import com.example.ui.theme.GlassTextMuted
import kotlinx.coroutines.launch

@Composable
fun AuthDialog(
    authRepository: AuthRepository,
    strings: AppStrings = AppStrings(AppLanguage.RU),
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xE614161C), Color(0xF208090C))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color(0x60FFFFFF), Color(0x14FFFFFF))),
                    RoundedCornerShape(30.dp)
                )
                .padding(26.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x28FFFFFF))
                        .border(1.5.dp, Color(0x80FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isSignUp) strings.signUpHeader else strings.signInHeader,
                    color = Color.White,
                    fontSize = 18.sp,
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

                Spacer(modifier = Modifier.height(18.dp))

                // Tab Switcher (Регистрация / Вход)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x12FFFFFF))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSignUp) Color(0x35FFFFFF) else Color.Transparent)
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
                            fontWeight = if (isSignUp) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isSignUp) Color(0x35FFFFFF) else Color.Transparent)
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
                            fontWeight = if (!isSignUp) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fields
                if (isSignUp) {
                    GlassTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = null
                        },
                        placeholder = strings.placeholderUsername,
                        leadingIcon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                GlassTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = strings.placeholderEmail,
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = strings.placeholderPassword,
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                AnimatedVisibility(visible = errorMessage != null) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x30FFFFFF))
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

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
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
                                    authRepository.signUp(username, email, password)
                                } else {
                                    authRepository.signIn(email, password)
                                }
                                isLoading = false
                                if (result.isSuccess) {
                                    onSuccess()
                                } else {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Auth error"
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
                        .padding(vertical = 8.dp)
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
