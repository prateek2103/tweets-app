import axios from "axios";
import React, { useRef } from "react";
import { useState } from "react";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
function Signup() {
  const onSubmitHandler = (e) => {
    e.preventDefault();
    let flag = true;
    let errorMessage;
    //confirm password and password should match
    if (formState.confirmPassword !== formState.password) {
      flag = false;
      errorMessage = "confirm password and password do not match";
    }

    //contact number should be less than
    if ((formState.phone + "").length != 10) {
      flag = false;
      errorMessage = "contact no. should have 10 digits";
    }

    if (!flag) {
      toast.error(errorMessage, {
        position: toast.POSITION.TOP_RIGHT,
      });
    } else {
      //prepare request body
      let reqBody = {
        firstName: formState.firstname,
        lastName: formState.lastname,
        contactNumber: formState.phone,
        username: formState.username,
        password: formState.password,
        email: formState.email,
      };

      axios
        .post("http://localhost:8080/register", reqBody)
        .then((res) => {
          toast.success(res.data, {
            position: toast.POSITION.TOP_RIGHT,
          });
          navigate("/login");
        })
        .catch((err) => {
          toast.error(err.response.data.errorMessage, {
            position: toast.POSITION.TOP_RIGHT,
          });
        });
    }
  };

  const formRef = useRef();
  const navigate = useNavigate();
  const [formState, setFormState] = useState({
    firstname: "",
    lastname: "",
    phone: "",
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
  });

  const onChangeHandler = (e) => {
    const { name, value } = e.target;
    setFormState({
      ...formState,
      [name]: value,
    });
  };

  return (
    <div>
      <form className="px-10 py-[10%]" onSubmit={onSubmitHandler} ref={formRef}>
        <div className="flex flex-row justify-between mb-5">
          <div className="flex-1">
            <input
              name="firstname"
              value={formState.firstname}
              className="input-lg border-2 border-slate-200 w-[90%] rounded-full mr-4 mb-4"
              placeholder="firstname"
              type="text"
              onChange={onChangeHandler}
              required
            />
          </div>
          <div className="flex-1">
            <input
              name="lastname"
              value={formState.lastname}
              className="input-lg border-2 border-slate-200 w-[90%] rounded-full mr-4 mb-4"
              placeholder="lastname"
              onChange={onChangeHandler}
              type="text"
            />
          </div>
        </div>

        <div className="flex flex-row justify-between  mb-5">
          <div className="flex w-100">
            <input
              name="phone"
              value={formState.phone}
              className="input-lg border-2 border-slate-200 w-[100%] rounded-full mr-4 mb-4"
              placeholder="phone"
              onChange={onChangeHandler}
              type="number"
              required
            />
          </div>
          <div className="flex-1 ">
            <input
              name="email"
              value={formState.email}
              className="input-lg border-2 border-slate-200 w-[100%] rounded-full mr-4 mb-4"
              placeholder="email"
              onChange={onChangeHandler}
              type="email"
              required
            />
          </div>
        </div>

        <input
          name="username"
          value={formState.username}
          className="input-lg border-2 border-slate-200 w-[70%] rounded-full mr-4 mb-10"
          placeholder="username"
          onChange={onChangeHandler}
          type="text"
          required
        />

        <input
          name="password"
          value={formState.password}
          className="input-lg border-2 border-slate-200 w-full rounded-full mb-10"
          placeholder="password"
          onChange={onChangeHandler}
          type="password"
          required
        />

        <input
          name="confirmPassword"
          value={formState.confirmPassword}
          className="input-lg border-2 border-slate-200 w-full rounded-full mb-8"
          placeholder="confirm password"
          onChange={onChangeHandler}
          type="password"
          required
        />

        <button className="btn w-4/12 mt-2 transition ease-linear hover:bg-tweeter-blue">
          Sign Up
        </button>
      </form>
    </div>
  );
}

export default Signup;
