#include <iostream>
#include <cmath>
#include <set>

#define epsilon 10e-7

double discriminant(double a, double b, double c) {
    return b * b - 4 * a * c;
}

double derivative_root_left(double a, double b, double c) {
    return (-1.0 * b - sqrt(discriminant(a, b, c))) / (2 * a);
}

double derivative_root_right(double a, double b, double c) {
    return (-1.0 * b + sqrt(discriminant(a, b, c))) / (2 * a);
}

double function_point(double x, double a, double b, double c) {
    return x * x * x + a * x * x + b * x + c;
}

double find_root(double start, double end, double a, double b, double c) {
    double root, k = (function_point(end, a, b, c) - function_point(start, a, b, c)) > 0 ? 1 : -1;
    while (fabs(end - start) >= epsilon) {
        root = (start + end) / 2;
        if (k * function_point(root, a, b, c) < 0) {
            start = root;
        } else {
            end = root;
        }
    }
    return root;
}

double find_edge(double point, double step, double a, double b, double c) {
    double k = step > 0 ? 1 : -1;
    while (k * function_point(point, a, b, c) < 0) {
        point += step;
    }
    return point;
}

int main(int argc, char **argv) {
    double a, b, c, discr,
            start, end, root;
    std::set<double> roots;

    a = 10;
    b = -9;
    c = -90;

    discr = discriminant(3, 2 * a, b);

    if (discr < 0 || fabs(discr) < epsilon) {
        if (fabs(function_point(0, a, b, c)) < epsilon) {
            roots.emplace(0.0);
        } else if (function_point(0, a, b, c) < 0) {
            start = end = 0.0;
            end = find_edge(end, 0.5, a, b, c);
            root = find_root(start, end, a, b, c);
            roots.emplace(root);
        } else {
            start = end = 0.0;
            start = find_edge(start, -0.5, a, b, c);
            root = find_root(start, end, a, b, c);
            roots.emplace(root);
        }
    } else {
        double root_left = derivative_root_left(3, 2 * a, b),
                root_right = derivative_root_right(3, 2 * a, b),
                extremum_min = function_point(root_left, a, b, c),
                extremum_max = function_point(root_right, a, b, c);

        if (fabs(extremum_min) < epsilon) {
            roots.emplace(root_left);
        }

        if (fabs(extremum_max) < epsilon) {
            roots.emplace(root_right);
        }

        if (extremum_min > 0) {
            start = end = root_left;
            start = find_edge(start, -0.05, a, b, c);
            root = find_root(start, end, a, b, c);
            roots.emplace(root);
        }

        if (extremum_max < 0) {
            start = end = root_right;
            end = find_edge(end, 0.05, a, b, c);
            root = find_root(start, end, a, b, c);
            roots.emplace(root);
        }

        if (extremum_max < 0 && extremum_min > 0) {
            start = root_left;
            end = root_right;
            root = find_root(start, end, a, b, c);
            roots.emplace(root);
        }
    }

    std::cout << "roots:\n\t";
    std::cout << std::fixed;
    std::cout.precision(6);

    for (auto i : roots) {
        std::cout << i << "\n\t";
    }

    return 0;
}
