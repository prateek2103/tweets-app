import React from "react";
import { Navigate } from "react-router-dom";

const RouteGuard = ({ children }) => {
  function hasJWT() {
    //check user has JWT token
    return localStorage.getItem("token") ? true : false;
  }

  if (!hasJWT()) {
    return <Navigate to="/login" />;
  }

  return children;
};

export default RouteGuard;
