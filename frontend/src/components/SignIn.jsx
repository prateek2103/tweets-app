import { ArrowForwardOutlined } from "@material-ui/icons";
import axios from "axios";
import React, { useRef } from "react";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../context/tweetsAction";

function SignIn() {
  const userRef = useRef();
  const passRef = useRef();
  const formRef = useRef();
  const navigate = useNavigate();

  const onSubmitHandler = async (e) => {
    e.preventDefault();

    //extract the username and password from the form
    let username = userRef.current.value;
    let password = passRef.current.value;

    //http request to server
    loginUser(username, password)
      .then((res) => {
        let { authToken, username, avatarUrl } = res.data;

        //store token and username in localStorage for later use
        localStorage.setItem("token", authToken);
        localStorage.setItem("username", username);
        localStorage.setItem("avatarUrl", avatarUrl);

        //success message
        toast.success(process.env.REACT_APP_SUCCESS_LOGIN_MSG);
      })
      .then((res) => {
        //navigate to home page on successful login
        navigate("/");
      })
      .catch((err) => {
        let errorMessage = "";
        let errorResponse = err.response;

        if (errorResponse.status === 401) {
          errorMessage = errorResponse.data.errorMessage;
        } else {
          errorMessage = "Server error. Please try again later.";
        }

        //error message
        toast.error(errorMessage);
      })
      .finally(() => {
        formRef.current.reset();
      });
  };

  return (
    <div className="container">
      <h1 className="font-bold text-5xl">Sign In</h1>
      <br />
      <form
        className="px-2 py-4 text-center"
        onSubmit={onSubmitHandler}
        ref={formRef}
      >
        <input
          ref={userRef}
          className="input-lg border-2 border-slate-200 focus:border-tweeter-blue linear w-full rounded-full mb-4"
          placeholder="username"
          type="text"
          required
        />
        <input
          ref={passRef}
          className="input-lg border-2 border-slate-200 w-full rounded-full mb-4"
          placeholder="password"
          type="password"
          required
        />

        <button className="btn w-4/12 mt-2 transition ease-linear hover:bg-tweeter-blue">
          <ArrowForwardOutlined />
        </button>
      </form>
    </div>
  );
}

export default SignIn;
