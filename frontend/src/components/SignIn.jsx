import { ArrowForwardOutlined } from "@material-ui/icons";
import axios from "axios";
import React, { useRef } from "react";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useNavigate } from "react-router-dom";

function SignIn() {
  const userRef = useRef();
  const passRef = useRef();
  const formRef = useRef();
  const navigate = useNavigate();

  const onSubmitHandler = async (e) => {
    e.preventDefault();

    let reqBody = {
      username: userRef.current.value,
      password: passRef.current.value,
    };

    let res = await axios
      .post("http://localhost:8080/login", reqBody)
      .then((res) => {
        let { authToken, username } = res.data;
        localStorage.setItem("token", authToken);
        localStorage.setItem("username", username);

        toast.success("logged in successfully", {
          position: toast.POSITION.TOP_RIGHT,
        });
      })
      .then((res) => {
        navigate("/");
      })
      .catch((err) => {
        console.log(err);
        let errorMessage;
        let errorResponse = err.response;

        if (errorResponse.status === 401) {
          errorMessage = errorResponse.data.errorMessage;
        } else {
          errorMessage = "Server error. Please try again later.";
        }

        toast.error(errorMessage, {
          position: toast.POSITION.TOP_RIGHT,
        });
      });

    formRef.current.reset();
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

      <ToastContainer />
    </div>
  );
}

export default SignIn;
