const loginForm = document.getElementById("login-form");

if (!loginForm) {
  console.error("loginForm 요소를 찾을 수 없습니다");
  throw new Error("loginForm not found");
}

function getLoginFormData() {
  const emailEl = document.getElementById("email");
  const passwordEl = document.getElementById("password");
  if (!emailEl || !passwordEl) {
    throw new Error("필수 입력 요소를 찾을 수 없습니다");
  }
  return {
    email: emailEl.value,
    password: passwordEl.value,
  };
}

function validateLoginData(loginData) {
  if (!loginData.email.trim()) {
    alert("이메일을 입력해주세요");
    return false;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(loginData.email)) {
    alert("올바른 이메일 형식을 입력해주세요.");
    return false;
  }

  if (!loginData.password.trim()) {
    alert("패스워드를 입력해주세요");
    return false;
  }

  return true;
}

async function submitLoginData(loginData) {
  const response = await fetch("/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(loginData),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => null);
    const message = errorData?.message || "로그인 요청 실패";
    throw new Error(message);
  }

  const data = response.json().catch(() => null);
  if (!data) {
    throw new Error("서버 응답 파싱 실패");
  }
  return data;
}

loginForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  const loginData = getLoginFormData();

  if (!validateLoginData(loginData)) {
    return;
  }

  try {
    const result = await submitLoginData(loginData);
    console.log("로그인 성공:", result);
    alert("로그인이 완료됐습니다.");
  } catch (error) {
    console.error("로그인 실패", error);
    alert(`로그인이 실패하였습니다: ${error.message}`);
  }
});
