package com.rexwong.thinkingdata

object Hello {
  def multiply(m: Int)(n: Int): Int = m * n
  def main(args: Array[String]) {
    println(multiply(2)(3))
    val timesTwo = multiply(2) _
    println(timesTwo(2))
  }

}