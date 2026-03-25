const signupForm = document.getElementById("signup-form");
if (!signupForm) {
  console.error("signupForm 요소를 찾을 수 없습니다");
  throw new Error("signup-form not found");
}

function getRequiredValue(id) {
  const el = document.getElementById(id);
  if (!el) throw new Error(`${id} 요소를 찾을 수 없습니다`);
  return el.value;
}

function getSignupFormData() {
  return {
    name: getRequiredValue("name"),
    email: getRequiredValue("email"),
    password: getRequiredValue("password"),
    address: getRequiredValue("address"),
    userRole: getRequiredValue("userRole"),
  };
}

function validateSignupData(signupData) {
  if (!signupData.name.trim()) {
    alert("이름을 입력해주세요.");
    return false;
  }

  if (!signupData.email.trim()) {
    alert("이메일을 입력해주세요.");
    return false;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(signupData.email)) {
    alert("올바른 이메일 형식을 입력해주세요.");
    return false;
  }

  if (!signupData.password.trim()) {
    alert("비밀번호를 입력해주세요.");
    return false;
  }

  if (!signupData.address.trim()) {
    alert("주소를 입력해주세요.");
    return false;
  }

  const validRoles = ["USER", "OWNER"];
  if (
    !signupData.userRole ||
    !validRoles.includes(signupData.userRole.toUpperCase())
  ) {
    alert("권한을 선택해주세요.");
    return false;
  }

  return true;
}

async function submitSignupData(signupData) {
  const response = await fetch("/auth/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(signupData),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => null);
    const message = errorData?.message || "회원가입 요청 실패";
    throw new Error(message);
  }

  const data = await response.json().catch(() => null);
  if (!data) {
    throw new Error("서버 응답 파싱 실패");
  }
  return data;
}

let isSubmitting = false;
signupForm.addEventListener("submit", async function (event) {
  event.preventDefault();
  if (isSubmitting) return;
  isSubmitting = true;
  const submitBtn = signupForm.querySelector('button[type="submit"]');
  if (submitBtn) submitBtn.disabled = true;

  const signupData = getSignupFormData();

  if (!validateSignupData(signupData)) {
    isSubmitting = false;
    if (submitBtn) submitBtn.disabled = false;
    return;
  }

  try {
    const result = await submitSignupData(signupData);
    console.log("회원가입 성공:", result);
    alert("회원가입이 완료됐습니다.");
    window.location.href = "/publicPage/login.html";
  } catch (error) {
    console.error("회원가입 실패", error);
    alert(`회원가입이 실패하였습니다: ${error.message}`);
  } finally {
    isSubmitting = false;
    if (submitBtn) submitBtn.disabled = false;
  }
});
