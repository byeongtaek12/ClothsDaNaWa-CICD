const signupForm = document.getElementById("signup-form");

function getSignupFormData() {
  return {
    name: document.getElementById("name").value,
    email: document.getElementById("email").value,
    password: document.getElementById("password").value,
    address: document.getElementById("address").value,
    userRole: document.getElementById("userRole").value,
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

  const validRoles = ["USER", "OWNER", "ADMIN"];
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

  return response.json();
}

signupForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  const signupData = getSignupFormData();

  if (!validateSignupData(signupData)) {
    return;
  }

  try {
    const result = await submitSignupData(signupData);
    console.log("회원가입 성공:", result);
    alert("회원가입이 완료됐습니다.");
  } catch (error) {
    console.error("회원가입 실패", error);
    alert(`회원가입이 실패하였습니다: ${error.message}`);
  }
});
